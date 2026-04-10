package edu.ntnu.idi.idatt2105.backend.common.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Container for authentication-related request and response DTOs used by the
 * authentication endpoints.
 */
public class AuthDtos {

  /**
   * Request payload for logging a user into the system using email and
   * password credentials.
   * <p>
   * Validation annotations ensure both fields are present and that the email
   * has a valid format.
   */
  public record LoginRequest(
      @NotBlank(message = "Email is required")
      @Email (message = "Must be a valid email")
      String email,

      @NotBlank (message = "Password is required")
      String password
  ) {}

  /**
   * Response payload returned after a successful login containing the issued
   * tokens and basic user information.
   */
  public record LoginResponse(
      String accessToken,
      String refreshToken,
      String email,
      String fullName,
      Long   organizationId,
      String role
  ) {}

  /**
   * Request payload used when registering a new user account.
   * <p>
   * Includes personal details, credentials and optional organization and
   * invitation metadata.
   */
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

  /**
   * Request payload used to exchange a refresh token for a new access token.
   */
  public record RefreshRequest(
      @NotBlank(message = "Refresh token is required")
      String refreshToken
  ) {}

  /**
   * Response payload containing a freshly issued access token.
   */
  public record RefreshResponse(
      String accessToken
  ) {}

}
