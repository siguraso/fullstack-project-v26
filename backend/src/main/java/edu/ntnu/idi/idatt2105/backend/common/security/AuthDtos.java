package edu.ntnu.idi.idatt2105.backend.common.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

  public record LoginRequest(
      @NotBlank(message = "Email is required")
      @Email (message = "Must be a valid email")
      String email,

      @NotBlank (message = "Password is required")
      String password
  ) {}

  public record LoginResponse(
      String accessToken,
      String refreshToken,
      String email,
      String fullName,
      Long   organizationId,
      String role
  ) {}

  public record RegisterRequest(
      @NotBlank(message = "Email is required")
      @Email(message = "Must be a valid email address")
      String email,

      @NotBlank(message = "Password is required")
      @Size(min = 8, message = "Password must be at least 8 characters")
      String password,

      @NotBlank(message = "First name is required")
      String firstName,

      @NotBlank(message = "Last name is required")
      String lastName,

      String orgNumber,

      String phone,

      String inviteToken
  ) {}

  public record RefreshRequest(
      @NotBlank(message = "Refresh token is required")
      String refreshToken
  ) {}

  public record RefreshResponse(
      String accessToken
  ) {}

}
