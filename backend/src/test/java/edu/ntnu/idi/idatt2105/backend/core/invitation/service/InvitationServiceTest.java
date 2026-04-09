package edu.ntnu.idi.idatt2105.backend.core.invitation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt2105.backend.common.email.EmailService;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
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
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TenantRepository tenantRepository;

  @Mock
  private InvitationRepository invitationRepository;

  @Mock
  private JwtService jwtService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private InvitationService invitationService;

  @BeforeEach
  void setUp() {
    TenantContext.setCurrentOrg(5L);
    ReflectionTestUtils.setField(invitationService, "inviteFrontendBaseUrl", "http://localhost:5173/");
    ReflectionTestUtils.setField(invitationService, "invitePath", "invite/accept");
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  void sendStaffInviteBuildsInviteUrlAndSavesInvitation() {
    Tenant tenant = new Tenant();
    tenant.setId(5L);
    tenant.setName("Regula AS");

    Instant expiry = Instant.parse("2026-04-10T10:15:30Z");

    when(userRepository.existsByEmail("new.staff@example.com")).thenReturn(false);
    when(tenantRepository.findById(5L)).thenReturn(Optional.of(tenant));
    when(jwtService.generateInviteToken("new.staff@example.com", 5L)).thenReturn("invite-jwt");
    when(jwtService.extractClaim(org.mockito.ArgumentMatchers.eq("invite-jwt"), any())).thenReturn(expiry);

    invitationService.sendStaffInvite("new.staff@example.com");

    ArgumentCaptor<Invitation> invitationCaptor = ArgumentCaptor.forClass(Invitation.class);
    verify(invitationRepository).save(invitationCaptor.capture());
    Invitation saved = invitationCaptor.getValue();

    assertEquals("new.staff@example.com", saved.getEmail());
    assertEquals("invite-jwt", saved.getToken());
    assertEquals(LocalDateTime.ofInstant(expiry, ZoneOffset.UTC), saved.getExpiresAt());

    String encodedToken = URLEncoder.encode("invite-jwt", StandardCharsets.UTF_8);
    String expectedUrl = "http://localhost:5173/invite/accept?token=" + encodedToken;

    verify(emailService).sendHtmlEmail(
        org.mockito.ArgumentMatchers.eq("new.staff@example.com"),
        org.mockito.ArgumentMatchers.contains("Regula AS"),
        org.mockito.ArgumentMatchers.contains(expectedUrl));
  }

  @Test
  void validateInviteTokenThrowsWhenTokenIsInvalid() {
    when(jwtService.isInviteToken("bad-token")).thenReturn(false);

    UnauthorizedException exception = assertThrows(
        UnauthorizedException.class,
        () -> invitationService.validateInviteToken("bad-token"));

    assertEquals("Invalid or expired invite token", exception.getMessage());
  }

  @Test
  void validateInviteTokenReturnsParsedInviteInfoWhenTokenIsValid() {
    when(jwtService.isInviteToken("ok-token")).thenReturn(true);
    when(jwtService.extractEmail("ok-token")).thenReturn("joiner@example.com");
    when(jwtService.extractOrganizationId("ok-token")).thenReturn(33L);

    InviteValidationResponse response = invitationService.validateInviteToken("ok-token");

    assertEquals(true, response.valid());
    assertEquals("joiner@example.com", response.email());
    assertEquals(33L, response.organizationId());
  }
}

