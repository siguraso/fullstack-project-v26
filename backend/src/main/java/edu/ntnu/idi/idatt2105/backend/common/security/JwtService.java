package edu.ntnu.idi.idatt2105.backend.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for issuing and validating JSON Web Tokens (JWT) used by
 * the application for authentication and invitation flows.
 * <p>
 * Supports three token types: access, refresh and invite tokens. Token
 * lifetimes and signing secret are configured via Spring properties under the
 * {@code security.jwt.*} namespace.
 */
@Slf4j
@Service
public class JwtService {
  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";
  private static final String TOKEN_TYPE_INVITE = "invite";

  /**
   * Secret string used to derive the HMAC signing key for all JWTs.
   */
  @Value("${security.jwt.secret}")
  private String jwtSecret;

  /**
   * Default expiration time for access tokens in milliseconds.
   */
  @Value("${security.jwt.expiration-ms}")
  private long jwtExpiration;

  /**
   * Expiration time for refresh tokens in milliseconds. Defaults to seven
   * days when not explicitly configured.
   */
  @Value("${jwt.refresh-expiration:604800000}")
  private long refreshExpiration;

  /**
   * Expiration time for invitation tokens in milliseconds. Defaults to 24
   * hours when not explicitly configured.
   */
  @Value("${security.jwt.invite-expiration-ms:86400000}")
  private long inviteExpiration;

  /**
   * Validates JWT configuration at startup and logs the resolved expiration
   * times for each token type.
   * <p>
   * Ensures that the secret is present and has a minimum length of 32 bytes,
   * which is required by the underlying signing algorithm.
   */
  @PostConstruct
  void logConfiguredExpirations() {
    if (jwtSecret == null || jwtSecret.isBlank()) {
      throw new IllegalStateException("security.jwt.secret must be configured");
    }

    if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
      throw new IllegalStateException("security.jwt.secret must be at least 32 bytes long");
    }

    logExpiration("security.jwt.expiration-ms", jwtExpiration);
    logExpiration("jwt.refresh-expiration", refreshExpiration);
    logExpiration("security.jwt.invite-expiration-ms", inviteExpiration);
  }

  /**
   * Logs the configured and normalized expiration value for a token-related
   * property.
   *
   * @param propertyName    name of the configuration property
   * @param configuredValue raw value as read from configuration
   */
  private void logExpiration(String propertyName, long configuredValue) {
    long resolvedValue = resolveExpirationMillis(propertyName, configuredValue);
    log.info("{} configured={}, resolved={} ms (~{} minutes)", propertyName, configuredValue,
        resolvedValue, resolvedValue / 60_000);
  }

  /**
   * Derives the HMAC signing key from the configured secret.
   *
   * @return a {@link Key} suitable for HS256 signing
   */
  private Key getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Generates a signed access token for the given user email and additional
   * claims.
   *
   * @param email       user identifier that will become the token subject
   * @param extraClaims additional claims to embed in the token payload
   * @return a compact JWT string representing an access token
   */
  public String generateToken(String email, Map<String, Object> extraClaims) {
    java.util.Map<String, Object> claims = new java.util.HashMap<>(extraClaims);
    claims.put("tokenType", TOKEN_TYPE_ACCESS);
    return buildToken(claims, email, resolveExpirationMillis("security.jwt.expiration-ms", jwtExpiration));
  }

  /**
   * Generates a signed refresh token for the given user email.
   *
   * @param email user identifier that will become the token subject
   * @return a compact JWT string representing a refresh token
   */
  public String generateRefreshToken(String email) {
    return buildToken(
        Map.of("tokenType", TOKEN_TYPE_REFRESH),
        email,
        resolveExpirationMillis("jwt.refresh-expiration", refreshExpiration));
  }

  /**
   * Generates an invitation token for the given email and organization.
   *
   * @param email          email address that will receive the invitation
   * @param organizationId identifier of the tenant/organization the invite is
   *                       associated with
   * @return a compact JWT string representing an invite token
   */
  public String generateInviteToken(String email, Long organizationId) {
    return buildToken(
        Map.of("invite", true, "organizationId", organizationId, "tokenType", TOKEN_TYPE_INVITE),
        email,
        resolveExpirationMillis("security.jwt.invite-expiration-ms", inviteExpiration));
  }

  /**
   * Normalizes a configured expiration value to milliseconds and guards
   * against invalid values.
   * <p>
   * Detects the common misconfiguration where values are given in seconds and
   * transparently converts them to milliseconds while logging a warning.
   *
   * @param propertyName    name of the configuration property
   * @param configuredValue raw value as read from configuration
   * @return the resolved expiration value in milliseconds
   */
  private long resolveExpirationMillis(String propertyName, long configuredValue) {
    if (configuredValue <= 0) {
      throw new IllegalStateException(propertyName + " must be greater than 0");
    }

    // Common misconfiguration: setting seconds (e.g. 3600) instead of milliseconds.
    if (configuredValue >= 60 && configuredValue <= 86_400 && configuredValue % 1_000 != 0) {
      long normalized = configuredValue * 1_000;
      log.warn("{}={} appears to be in seconds; treating it as {} ms", propertyName, configuredValue,
          normalized);
      return normalized;
    }

    return configuredValue;
  }

  /**
   * Checks whether the given token is a valid invite token.
   *
   * @param token JWT to inspect
   * @return {@code true} if the token is valid and contains the invite claim,
   *     {@code false} otherwise
   */
  public boolean isInviteToken(String token) {
    if (!isTokenValid(token)) {
      return false;
    }
    Boolean inviteClaim = extractClaim(token, claims -> claims.get("invite", Boolean.class));
    return Boolean.TRUE.equals(inviteClaim);
  }

  /**
   * Validates that the provided token is a non-expired invite token belonging
   * to the given email.
   *
   * @param token JWT invite token
   * @param email expected email contained in the token subject
   * @return {@code true} if the token is a valid invite for the email
   */
  public boolean isInviteTokenValid(String token, String email) {
    if (!isTokenValid(token) || !isInviteToken(token)) {
      return false;
    }
    return isTokenValid(token, email);
  }

  /**
   * Builds and signs a JWT with the given claims, subject and expiration
   * duration.
   *
   * @param extraClaims additional claims to include in the token
   * @param email       subject for the token, typically the user email
   * @param expiration  lifetime of the token in milliseconds
   * @return a compact JWT string
   */
  private String buildToken(Map<String, Object> extraClaims, String email, long expiration) {
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extracts the subject (email) from the given token.
   *
   * @param token JWT to inspect
   * @return email stored as the token subject
   */
  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts the role claim from the given token.
   *
   * @param token JWT to inspect
   * @return role name stored in the {@code role} claim, or {@code null} if absent
   */
  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  /**
   * Extracts the token type from the given token.
   *
   * @param token JWT to inspect
   * @return token type (e.g. access, refresh, invite) or {@code null} if absent
   */
  public String extractTokenType(String token) {
    return extractClaim(token, claims -> claims.get("tokenType", String.class));
  }

  /**
   * Extracts the organization identifier from the given token.
   *
   * @param token JWT to inspect
   * @return organization id stored in the {@code organizationId} claim, or
   *     {@code null} if absent
   */
  public Long extractOrganizationId(String token) {
    return extractClaim(token, claims -> claims.get("organizationId", Long.class));
  }

  /**
   * Extracts an arbitrary claim from the token using the provided resolver
   * function.
   *
   * @param token          JWT to inspect
   * @param claimsResolver function that maps {@link Claims} to the desired
   *                       value
   * @param <T>            type of the extracted value
   * @return the resolved claim value
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Verifies the structural integrity and signature of a JWT without checking
   * its subject or type.
   *
   * @param token JWT to validate
   * @return {@code true} if the token can be parsed and verified, otherwise
   *     {@code false}
   */
  public boolean isTokenValid(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Validates that the token belongs to the specified email and has not
   * expired.
   *
   * @param token JWT to validate
   * @param email expected subject contained in the token
   * @return {@code true} if the token is structurally valid, not expired and
   *     its subject matches the email
   */
  public boolean isTokenValid(String token, String email) {
    final String tokenEmail = extractEmail(token);
    return (tokenEmail.equals(email)) && !isTokenExpired(token);
  }

  /**
   * Checks whether the token is a valid refresh token.
   *
   * @param token JWT to inspect
   * @return {@code true} if the token is valid and marked as a refresh token
   */
  public boolean isRefreshToken(String token) {
    if (!isTokenValid(token)) {
      return false;
    }

    return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
  }

  /**
   * Determines whether the token has passed its expiration date.
   *
   * @param token JWT to inspect
   * @return {@code true} if the token is expired, {@code false} otherwise
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Extracts the expiration timestamp from the given token.
   *
   * @param token JWT to inspect
   * @return the expiration {@link Date}
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Parses the token and returns the full set of claims.
   *
   * @param token JWT to inspect
   * @return the {@link Claims} contained in the token
   */
  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
