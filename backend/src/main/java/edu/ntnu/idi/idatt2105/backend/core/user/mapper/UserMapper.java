package edu.ntnu.idi.idatt2105.backend.core.user.mapper;

import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import org.springframework.stereotype.Component;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;

@Component
public class UserMapper {

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
