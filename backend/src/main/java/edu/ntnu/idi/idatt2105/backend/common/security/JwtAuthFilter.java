package edu.ntnu.idi.idatt2105.backend.common.security;

import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.jspecify.annotations.NonNull;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      authenticateIfPossible(request);
    } catch (JwtException e) {
      logger.error("JWT validation failed: " + e.getMessage());
      SecurityContextHolder.clearContext();
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }

  private void authenticateIfPossible(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }

    String jwt = authHeader.substring(7);
    if (!jwtService.isTokenValid(jwt)) {
      return;
    }

    String email = jwtService.extractEmail(jwt);
    if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
      return;
    }

    var user = userRepository.findByEmail(email).orElse(null);
    if (user == null || !user.isActive()) {
      return;
    }

    Long organizationId = jwtService.extractOrganizationId(jwt);
    if (organizationId != null) {
      TenantContext.setCurrentOrg(organizationId);
    }

    String role = jwtService.extractRole(jwt);
    List<SimpleGrantedAuthority> authorities = role != null ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
        : Collections.emptyList();

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null,
        authorities);
    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  }
}
