package edu.ntnu.idi.idatt2105.backend.common.security;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing authentication-related endpoints such as login,
 * registration and token refresh.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for login, registration and token refresh")
public class AuthController {

  private final AuthService authService;

  /**
   * Authenticates a user with email and password and returns access and refresh
   * tokens on success.
   *
   * @param request the login request payload
   * @return a 200 OK response containing tokens and basic user information
   */
  @PostMapping("/login")
  @Operation(summary = "Log in", description = "Authenticates a user and returns access/refresh tokens")
  public ResponseEntity<ApiResponse<AuthDtos.LoginResponse>> login(
      @Valid @RequestBody AuthDtos.LoginRequest request) {
    AuthDtos.LoginResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  /**
   * Creates a new user account for an existing tenant and returns initial
   * authentication tokens.
   *
   * @param request the registration request payload
   * @return a 201 Created response containing tokens for the registered user
   */
  @PostMapping("/register")
  @Operation(summary = "Register user", description = "Creates a user account and returns tokens")
  public ResponseEntity<ApiResponse<AuthDtos.LoginResponse>> register(
      @Valid @RequestBody AuthDtos.RegisterRequest request) {
    AuthDtos.LoginResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User registered successfully", response));
  }

  /**
   * Exchanges a valid refresh token for a new access token.
   *
   * @param request the refresh request payload containing the refresh token
   * @return a 200 OK response with a new access token
   */
  @PostMapping("/refresh")
  @Operation(summary = "Refresh access token", description = "Issues a new access token from a valid refresh token")
  public ResponseEntity<ApiResponse<AuthDtos.RefreshResponse>> refresh(
      @Valid @RequestBody AuthDtos.RefreshRequest request) {
    AuthDtos.RefreshResponse response = authService.refresh(request);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }
}
