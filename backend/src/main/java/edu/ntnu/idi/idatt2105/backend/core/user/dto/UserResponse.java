package edu.ntnu.idi.idatt2105.backend.core.user.dto;

import lombok.Data;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;

/**
 * DTO representing a user in the system.
 *
 * <p>
 * Used to return user information to the frontend, including
 * identity, contact details, role, and status.
 * </p>
 */
@Data
public class UserResponse {

  private Long id;
  private Long tenantId;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private String phone;
  private UserRole role;
  private boolean active;
}
