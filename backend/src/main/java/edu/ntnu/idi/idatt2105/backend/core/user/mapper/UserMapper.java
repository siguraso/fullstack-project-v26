package edu.ntnu.idi.idatt2105.backend.core.user.mapper;

import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import org.springframework.stereotype.Component;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;

/**
 * Mapper that converts {@link User} entities to {@link UserResponse} DTOs.
 */
@Component
public class UserMapper {

  /**
   * Converts a user entity to its response DTO representation.
   *
   * @param user the user entity
   * @return the corresponding {@link UserResponse}
   */
  public UserResponse toResponse(User user) {
    UserResponse dto = new UserResponse();
    dto.setId(user.getId());
    dto.setTenantId(user.getTenant().getId());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setPhone(user.getPhone());
    dto.setRole(user.getRole());
    dto.setActive(user.isActive());
    return dto;
  }

}
