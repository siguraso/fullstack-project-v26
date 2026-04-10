package edu.ntnu.idi.idatt2105.backend.core.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;

/**
 * Request DTO used to create a new user.
 *
 * <p>
 * Contains user credentials, personal information, and role assignment.
 * Typically used by admins or during onboarding flows.
 * </p>
 */
@Data
public class UserCreateRequest {

  @NotBlank
  private String username;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  @Email
  private String email;

  @NotBlank
  private String password;

  private UserRole role;
}
