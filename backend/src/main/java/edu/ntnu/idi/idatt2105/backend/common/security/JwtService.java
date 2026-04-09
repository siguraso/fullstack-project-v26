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

@Slf4j
@Service
public class JwtService {
  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";
  private static final String TOKEN_TYPE_INVITE = "invite";

  @Value("${security.jwt.secret}")
  private String jwtSecret;

  @Value("${security.jwt.expiration-ms}")
  private long jwtExpiration;

  @Value("${jwt.refresh-expiration:604800000}")
  private long refreshExpiration;

  @Value("${security.jwt.invite-expiration-ms:86400000}")
  private long inviteExpiration;

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

  private void logExpiration(String propertyName, long configuredValue) {
    long resolvedValue = resolveExpirationMillis(propertyName, configuredValue);
    log.info("{} configured={}, resolved={} ms (~{} minutes)", propertyName, configuredValue,
        resolvedValue, resolvedValue / 60_000);
  }

  private Key getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String email, Map<String, Object> extraClaims) {
    java.util.Map<String, Object> claims = new java.util.HashMap<>(extraClaims);
    claims.put("tokenType", TOKEN_TYPE_ACCESS);
    return buildToken(claims, email, resolveExpirationMillis("security.jwt.expiration-ms", jwtExpiration));
  }

  public String generateRefreshToken(String email) {
    return buildToken(
        Map.of("tokenType", TOKEN_TYPE_REFRESH),
        email,
        resolveExpirationMillis("jwt.refresh-expiration", refreshExpiration));
  }

  public String generateInviteToken(String email, Long organizationId) {
    return buildToken(
        Map.of("invite", true, "organizationId", organizationId, "tokenType", TOKEN_TYPE_INVITE),
        email,
        resolveExpirationMillis("security.jwt.invite-expiration-ms", inviteExpiration));
  }

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

  public boolean isInviteToken(String token) {
    if (!isTokenValid(token)) {
      return false;
    }
    Boolean inviteClaim = extractClaim(token, claims -> claims.get("invite", Boolean.class));
    return Boolean.TRUE.equals(inviteClaim);
  }

  public boolean isInviteTokenValid(String token, String email) {
    if (!isTokenValid(token) || !isInviteToken(token)) {
      return false;
    }
    return isTokenValid(token, email);
  }

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

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  public String extractTokenType(String token) {
    return extractClaim(token, claims -> claims.get("tokenType", String.class));
  }

  public Long extractOrganizationId(String token) {
    return extractClaim(token, claims -> claims.get("organizationId", Long.class));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

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

  public boolean isTokenValid(String token, String email) {
    final String tokenEmail = extractEmail(token);
    return (tokenEmail.equals(email)) && !isTokenExpired(token);
  }

  public boolean isRefreshToken(String token) {
    if (!isTokenValid(token)) {
      return false;
    }

    return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
