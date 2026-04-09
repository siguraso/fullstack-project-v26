package edu.ntnu.idi.idatt2105.backend.core.invitation.service;

import edu.ntnu.idi.idatt2105.backend.common.email.EmailService;
import edu.ntnu.idi.idatt2105.backend.common.email.EmailTemplate;
import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.common.security.JwtService;
import edu.ntnu.idi.idatt2105.backend.core.invitation.dto.InviteValidationResponse;
import edu.ntnu.idi.idatt2105.backend.core.invitation.entity.Invitation;
import edu.ntnu.idi.idatt2105.backend.core.invitation.repository.InvitationRepository;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {
  private final UserRepository userRepository;
  private final TenantRepository tenantRepository;
  private final InvitationRepository invitationRepository;
  private final JwtService jwtService;
  private final EmailService emailService;

  @Value("${email.invite.frontend-base-url:http://localhost:5173}")
  private String inviteFrontendBaseUrl;

  @Value("${email.invite.path:/invite/accept}")
  private String invitePath;

  @Transactional
  public void sendStaffInvite(String email) {
	log.info("Sending staff invitation for {}", maskEmail(email));
	if (userRepository.existsByEmail(email)) {
	  log.warn("Invitation rejected: user already exists for {}", maskEmail(email));
	  throw new ValidationException("Email already exists: " + email);
	}

	Long currentTenantId = TenantContext.getCurrentOrg();
	Tenant tenant = tenantRepository.findById(currentTenantId)
		.orElseThrow(() -> new ResourceNotFoundException("Tenant ID not found"));

	String token = jwtService.generateInviteToken(email, currentTenantId);
	String inviteUrl = buildInviteUrl(token);

	Invitation invitation = new Invitation();
	invitation.setTenant(tenant);
	invitation.setEmail(email);
	invitation.setToken(token);
	invitation.setExpiresAt(resolveTokenExpiry(token));
	invitationRepository.save(invitation);

	String subject = "You are invited to join " + tenant.getName();
	String htmlBody = EmailTemplate.invitationEmail(tenant.getName(), inviteUrl);
	emailService.sendHtmlEmail(email, subject, htmlBody);

	log.info("Invitation sent for tenantId={} recipient={}", currentTenantId, maskEmail(email));
  }

  @Transactional(readOnly = true)
  public InviteValidationResponse validateInviteToken(String token) {
	log.info("Validating invite token");
	if (!jwtService.isInviteToken(token)) {
	  log.warn("Invite token validation failed: token is invalid or expired");
	  throw new UnauthorizedException("Invalid or expired invite token");
	}

	String email = jwtService.extractEmail(token);
	Long organizationId = jwtService.extractOrganizationId(token);
	if (organizationId == null) {
	  log.warn("Invite token validation failed: missing organization context");
	  throw new UnauthorizedException("Invite token is missing organization context");
	}

	log.info("Invite token validated for tenantId={} recipient={}", organizationId, maskEmail(email));

	return new InviteValidationResponse(true, email, organizationId);
  }

  private String buildInviteUrl(String token) {
	String base = trimTrailingSlash(inviteFrontendBaseUrl);
	String path = invitePath.startsWith(AntPathMatcher.DEFAULT_PATH_SEPARATOR)
		? invitePath
		: AntPathMatcher.DEFAULT_PATH_SEPARATOR + invitePath;
	return base + path + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
  }

  private String trimTrailingSlash(String value) {
	if (value.endsWith("/")) {
	  return value.substring(0, value.length() - 1);
	}
	return value;
  }

  private LocalDateTime resolveTokenExpiry(String token) {
	Instant expiry = jwtService.extractClaim(token, claims -> claims.getExpiration().toInstant());
	return LocalDateTime.ofInstant(expiry, ZoneOffset.UTC);
  }

  private String maskEmail(String email) {
	if (email == null || email.isBlank()) {
	  return "<unknown>";
	}

	int atIndex = email.indexOf('@');
	if (atIndex <= 1) {
	  return "***";
	}

	return email.charAt(0) + "***" + email.substring(atIndex);
  }

}
