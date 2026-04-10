package edu.ntnu.idi.idatt2105.backend.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private JwtAuthFilter jwtAuthFilter;

  @AfterEach
  void clearContexts() {
    SecurityContextHolder.clearContext();
    TenantContext.clear();
  }

  @Test
  void doFilterInternalAuthenticatesValidBearerTokenAndClearsTenantAfterChain() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    User activeUser = new User();
    activeUser.setEmail("admin@example.com");
    activeUser.setActive(true);

    when(jwtService.isTokenValid("valid-token")).thenReturn(true);
    when(jwtService.extractEmail("valid-token")).thenReturn("admin@example.com");
    when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(activeUser));
    when(jwtService.extractOrganizationId("valid-token")).thenReturn(12L);
    when(jwtService.extractRole("valid-token")).thenReturn("ADMIN");

    AtomicBoolean chainCalled = new AtomicBoolean(false);
    AtomicReference<Authentication> authInsideChain = new AtomicReference<>();
    AtomicReference<Long> tenantInsideChain = new AtomicReference<>();

    jwtAuthFilter.doFilterInternal(request, response, (req, res) -> {
      chainCalled.set(true);
      authInsideChain.set(SecurityContextHolder.getContext().getAuthentication());
      tenantInsideChain.set(TenantContext.getCurrentOrg());
    });

    assertTrue(chainCalled.get());
    assertNotNull(authInsideChain.get());
    assertEquals("admin@example.com", authInsideChain.get().getName());
    assertEquals("ROLE_ADMIN", authInsideChain.get().getAuthorities().iterator().next().getAuthority());
    assertEquals(12L, tenantInsideChain.get());

    assertThrows(UnauthorizedException.class, TenantContext::getCurrentOrg);
  }

  @Test
  void doFilterInternalSkipsAuthenticationWhenAuthorizationHeaderIsMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    AtomicReference<Authentication> authInsideChain = new AtomicReference<>();

    jwtAuthFilter.doFilterInternal(request, response, (req, res) ->
        authInsideChain.set(SecurityContextHolder.getContext().getAuthentication()));

    assertNull(authInsideChain.get());
  }

  @Test
  void doFilterInternalClearsSecurityContextWhenJwtLibraryThrows() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer broken-token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    SecurityContextHolder.getContext().setAuthentication(
        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("old", "n/a"));

    when(jwtService.isTokenValid("broken-token")).thenThrow(new JwtException("bad token"));

    AtomicReference<Authentication> authInsideChain = new AtomicReference<>();

    jwtAuthFilter.doFilterInternal(request, response, (req, res) ->
        authInsideChain.set(SecurityContextHolder.getContext().getAuthentication()));

    assertNull(authInsideChain.get());
  }
}

