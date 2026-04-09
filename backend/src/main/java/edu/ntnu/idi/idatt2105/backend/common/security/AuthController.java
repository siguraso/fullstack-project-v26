package edu.ntnu.idi.idatt2105.backend.common.security;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthDtos.LoginResponse>> login(
      @Valid @RequestBody AuthDtos.LoginRequest request) {
    AuthDtos.LoginResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthDtos.LoginResponse>> register(
      @Valid @RequestBody AuthDtos.RegisterRequest request) {
    AuthDtos.LoginResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User registered successfully", response));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthDtos.RefreshResponse>> refresh(
      @Valid @RequestBody AuthDtos.RefreshRequest request) {
    AuthDtos.RefreshResponse response = authService.refresh(request);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }
}

