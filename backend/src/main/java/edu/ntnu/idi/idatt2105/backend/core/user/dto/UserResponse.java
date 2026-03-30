package edu.ntnu.idi.idatt2105.backend.core.user.dto;

import lombok.Data;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;

@Data
public class UserResponse {

  private Long id;
  private Long tenantId;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private UserRole role;
}
