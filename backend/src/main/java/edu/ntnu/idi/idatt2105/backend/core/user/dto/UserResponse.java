package edu.ntnu.idi.idatt2105.backend.core.user.dto;

import lombok.Data;

@Data
public class UserResponse {

  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
}
