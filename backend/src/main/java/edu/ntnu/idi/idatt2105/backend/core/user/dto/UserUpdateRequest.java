package edu.ntnu.idi.idatt2105.backend.core.user.dto;

import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO used to update an existing user.
 *
 * <p>
 * Contains updated personal information and role assignment.
 * All fields are required when performing a full update.
 * </p>
 */
@Data
public class UserUpdateRequest {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Phone is required")
  private String phone;

  @NotNull(message = "Role is required")
  private UserRole role;
}
