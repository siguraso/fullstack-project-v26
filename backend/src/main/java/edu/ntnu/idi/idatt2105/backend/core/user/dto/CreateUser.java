package edu.ntnu.idi.idatt2105.backend.core.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;

@Data
public class CreateUser {

  @NotNull
  private Long tenant_id; // TODO: set to long?

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
