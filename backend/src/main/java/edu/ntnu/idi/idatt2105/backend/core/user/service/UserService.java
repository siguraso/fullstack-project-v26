package edu.ntnu.idi.idatt2105.backend.core.user.service;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.core.user.dto.CreateUser;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserResponse createUser(CreateUser dto) {
    User user = new User();

    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());

    User saved = userRepository.save(user);

    return mapToDTO(saved);
  }

  private UserResponse mapToDTO(User user) {
    UserResponse dto = new UserResponse();
    dto.setId(user.getId());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    return dto;
  }

}
