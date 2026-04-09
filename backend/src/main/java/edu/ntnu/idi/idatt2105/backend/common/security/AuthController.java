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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for login, registration and token refresh")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @Operation(summary = "Log in", description = "Authenticates a user and returns access/refresh tokens")
  public ResponseEntity<ApiResponse<AuthDtos.LoginResponse>> login(
      @Valid @RequestBody AuthDtos.LoginRequest request) {
    AuthDtos.LoginResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  @PostMapping("/register")
  @Operation(summary = "Register user", description = "Creates a user account and returns tokens")
  public ResponseEntity<ApiResponse<AuthDtos.LoginResponse>> register(
      @Valid @RequestBody AuthDtos.RegisterRequest request) {
    AuthDtos.LoginResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User registered successfully", response));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Refresh access token", description = "Issues a new access token from a valid refresh token")
  public ResponseEntity<ApiResponse<AuthDtos.RefreshResponse>> refresh(
      @Valid @RequestBody AuthDtos.RefreshRequest request) {
    AuthDtos.RefreshResponse response = authService.refresh(request);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }
}
