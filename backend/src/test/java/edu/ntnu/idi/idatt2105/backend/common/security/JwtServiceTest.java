package edu.ntnu.idi.idatt2105.backend.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "jwtSecret", "XiIGnXyfdw7Fw7q86QJe5XGyWNipkARsUshFlrPmTZx");
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);
    ReflectionTestUtils.setField(jwtService, "refreshExpiration", 7_200_000L);
    ReflectionTestUtils.setField(jwtService, "inviteExpiration", 86_400_000L);
    jwtService.logConfiguredExpirations();
  }

  @Test
  void generateRefreshTokenCreatesValidRefreshTokenType() {
    String refreshToken = jwtService.generateRefreshToken("staff@example.com");

    assertTrue(jwtService.isTokenValid(refreshToken));
    assertTrue(jwtService.isRefreshToken(refreshToken));
    assertEquals("refresh", jwtService.extractTokenType(refreshToken));
  }

  @Test
  void inviteTokenValidationRequiresMatchingEmailAndInviteType() {
    String inviteToken = jwtService.generateInviteToken("invitee@example.com", 17L);

    assertTrue(jwtService.isInviteToken(inviteToken));
    assertTrue(jwtService.isInviteTokenValid(inviteToken, "invitee@example.com"));
    assertFalse(jwtService.isInviteTokenValid(inviteToken, "other@example.com"));
    assertEquals(17L, jwtService.extractOrganizationId(inviteToken));
  }
}

