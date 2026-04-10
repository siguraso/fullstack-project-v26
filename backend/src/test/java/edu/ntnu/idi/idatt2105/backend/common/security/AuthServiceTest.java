package edu.ntnu.idi.idatt2105.backend.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TenantRepository tenantRepository;

  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthService authService;

  @Test
  void loginReturnsTokensAndUserDetailsWhenCredentialsAreValid() {
    Tenant tenant = new Tenant();
    tenant.setId(77L);

    User user = new User();
    user.setId(11L);
    user.setEmail("staff@example.com");
    user.setPassword("encoded-password");
    user.setFirstName("Ada");
    user.setLastName("Lovelace");
    user.setRole(UserRole.STAFF);
    user.setTenant(tenant);
    user.setActive(true);

    AuthDtos.LoginRequest request = new AuthDtos.LoginRequest("staff@example.com", "plain-password");

    when(userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);
    when(jwtService.generateToken(eq("staff@example.com"), anyMap())).thenReturn("access-token");
    when(jwtService.generateRefreshToken("staff@example.com")).thenReturn("refresh-token");

    AuthDtos.LoginResponse response = authService.login(request);

    assertEquals("access-token", response.accessToken());
    assertEquals("refresh-token", response.refreshToken());
    assertEquals("Ada Lovelace", response.fullName());
    assertEquals(77L, response.organizationId());
    assertEquals("STAFF", response.role());

    ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(jwtService).generateToken(eq("staff@example.com"), claimsCaptor.capture());
    assertEquals(11L, claimsCaptor.getValue().get("userId"));
    assertEquals(77L, claimsCaptor.getValue().get("organizationId"));
    assertEquals("STAFF", claimsCaptor.getValue().get("role"));
  }

  @Test
  void registerWithInviteTokenCreatesStaffUserInInvitedTenant() {
    Tenant tenant = new Tenant();
    tenant.setId(42L);

    AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest(
        "invitee@example.com",
        "StrongPassword123",
        "Grace",
        "Hopper",
        null,
        "93000000",
        "invite-token");

    when(userRepository.findByEmail("invitee@example.com")).thenReturn(Optional.empty());
    when(jwtService.isInviteTokenValid("invite-token", "invitee@example.com")).thenReturn(true);
    when(jwtService.extractOrganizationId("invite-token")).thenReturn(42L);
    when(tenantRepository.findById(42L)).thenReturn(Optional.of(tenant));
    when(passwordEncoder.encode("StrongPassword123")).thenReturn("encoded-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User saved = invocation.getArgument(0);
      saved.setId(123L);
      return saved;
    });
    when(jwtService.generateToken(eq("invitee@example.com"), anyMap())).thenReturn("access-token");
    when(jwtService.generateRefreshToken("invitee@example.com")).thenReturn("refresh-token");

    AuthDtos.LoginResponse response = authService.register(request);

    assertEquals("invitee@example.com", response.email());
    assertEquals(42L, response.organizationId());
    assertEquals("STAFF", response.role());

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("invitee@example.com", savedUser.getEmail());
    assertEquals("encoded-password", savedUser.getPassword());
    assertTrue(savedUser.isActive());
    assertEquals(UserRole.STAFF, savedUser.getRole());
    assertEquals(tenant, savedUser.getTenant());
  }

  @Test
  void registerWithoutOrgNumberThrowsUnauthorizedException() {
    AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest(
        "newuser@example.com",
        "StrongPassword123",
        "Alan",
        "Turing",
        " ",
        "93000000",
        null);

    when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

    UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.register(request));

    assertEquals("Organisation number is required", exception.getMessage());
  }

  @Test
  void refreshThrowsWhenTokenIsNotARefreshToken() {
    AuthDtos.RefreshRequest request = new AuthDtos.RefreshRequest("bad-token");
    when(jwtService.isRefreshToken("bad-token")).thenReturn(false);

    UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.refresh(request));

    assertEquals("Invalid refresh token", exception.getMessage());
  }

  @Test
  void refreshThrowsWhenUserFromRefreshTokenDoesNotExist() {
    AuthDtos.RefreshRequest request = new AuthDtos.RefreshRequest("refresh-token");
    when(jwtService.isRefreshToken("refresh-token")).thenReturn(true);
    when(jwtService.extractEmail("refresh-token")).thenReturn("missing@example.com");
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> authService.refresh(request));

    assertEquals("User not found", exception.getMessage());
  }
}

