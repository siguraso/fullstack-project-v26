package edu.ntnu.idi.idatt2105.backend.common.security;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final TenantRepository tenantRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
    log.info("Authenticating user with email: {}", request.email());

    // Find user by email
    Optional<User> optionalUser = userRepository.findByEmail(request.email());
    if (optionalUser.isEmpty()) {
      log.warn("User not found with email: {}", request.email());
      throw new UnauthorizedException("Invalid email or password");
    }

    User user = optionalUser.get();

    // Check if user is active
    if (!user.isActive()) {
      log.warn("User account is inactive: {}", request.email());
      throw new UnauthorizedException("User account is inactive");
    }

    // Verify password
    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      log.warn("Invalid password for user: {}", request.email());
      throw new UnauthorizedException("Invalid email or password");
    }

    // Generate tokens
    Map<String, Object> extraClaims = Map.of(
        "userId", user.getId(),
        "organizationId", user.getTenant().getId(),
        "firstName", user.getFirstName(),
        "lastName", user.getLastName(),
        "role", user.getRole().name());

    String accessToken = jwtService.generateToken(user.getEmail(), extraClaims);
    String refreshToken = jwtService.generateRefreshToken(user.getEmail());

    log.info("User authenticated successfully: {}", request.email());

    return new AuthDtos.LoginResponse(
        accessToken,
        refreshToken,
        user.getEmail(),
        user.getFirstName() + " " + user.getLastName(),
        user.getTenant().getId(),
        user.getRole().name());
  }

  public AuthDtos.LoginResponse register(AuthDtos.RegisterRequest request) {
    log.info("Registering new user with email: {}", request.email());

    // Check if user already exists
    if (userRepository.findByEmail(request.email()).isPresent()) {
      log.warn("User already exists with email: {}", request.email());
      throw new UnauthorizedException("Email already registered");
    }

    Tenant tenant;
    String inviteToken = request.inviteToken();
    if (inviteToken != null && !inviteToken.isBlank()) {
      if (!jwtService.isInviteTokenValid(inviteToken, request.email())) {
        log.warn("Invalid invite token used for email: {}", request.email());
        throw new UnauthorizedException("Invalid or expired invite token");
      }

      Long tenantId = jwtService.extractOrganizationId(inviteToken);
      if (tenantId == null) {
        throw new UnauthorizedException("Invite token is missing organization context");
      }

      tenant = tenantRepository.findById(tenantId)
          .orElseThrow(() -> new UnauthorizedException("Tenant does not exist"));
    } else {
      if (request.orgNumber() == null || request.orgNumber().isBlank()) {
        throw new UnauthorizedException("Organisation number is required");
      }

      tenant = tenantRepository.findByOrgNumber(request.orgNumber());
      if (tenant == null) {
        log.warn("Tenant with org: {} does not exist.", request.orgNumber());
        throw new UnauthorizedException("Tenant does not exist");
      }
    }

    // Create new user
    User user = new User();
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setPhone(request.phone());
    user.setUsername(request.email()); // Use email as username
    user.setTenant(tenant);
    user.setActive(true);
    user.setRole(UserRole.STAFF);

    user = userRepository.save(user);
    log.info("User registered successfully with ID: {}", user.getId());

    // Generate tokens
    Map<String, Object> extraClaims = Map.of(
        "userId", user.getId(),
        "organizationId", tenant.getId(),
        "firstName", user.getFirstName(),
        "lastName", user.getLastName(),
        "role", user.getRole().name());

    String accessToken = jwtService.generateToken(user.getEmail(), extraClaims);
    String refreshToken = jwtService.generateRefreshToken(user.getEmail());

    return new AuthDtos.LoginResponse(
        accessToken,
        refreshToken,
        user.getEmail(),
        user.getFirstName() + " " + user.getLastName(),
        tenant.getId(),
        user.getRole().name());
  }

  public AuthDtos.RefreshResponse refresh(AuthDtos.RefreshRequest request) {
    log.info("Refreshing access token");

    String refreshToken = request.refreshToken();

    if (!jwtService.isTokenValid(refreshToken)) {
      log.warn("Invalid refresh token");
      throw new UnauthorizedException("Invalid refresh token");
    }

    String email = jwtService.extractEmail(refreshToken);

    Optional<User> optionalUser = userRepository.findByEmail(email);
    if (optionalUser.isEmpty()) {
      log.warn("User not found for refresh token: {}", email);
      throw new ResourceNotFoundException("User not found");
    }

    User user = optionalUser.get();

    if (!user.isActive()) {
      log.warn("Inactive user attempted token refresh: {}", email);
      throw new UnauthorizedException("User account is inactive");
    }

    // Generate new access token
    Map<String, Object> extraClaims = Map.of(
        "userId", user.getId(),
        "organizationId", user.getTenant().getId(),
        "firstName", user.getFirstName(),
        "lastName", user.getLastName(),
        "role", user.getRole().name());

    String accessToken = jwtService.generateToken(user.getEmail(), extraClaims);
    log.info("Access token refreshed successfully for user: {}", email);

    return new AuthDtos.RefreshResponse(accessToken);
  }
}
