package edu.ntnu.idi.idatt2105.backend.core.user.service;

import org.springframework.stereotype.Service;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.CreateUser;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final TenantRepository tenantRepository;

  public UserService(UserRepository userRepository, TenantRepository tenantRepository) {
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
  }

  public UserResponse createUser(CreateUser dto) {
    User user = new User();
    Tenant tenant = tenantRepository.findById(dto.getTenant_id())
        .orElseThrow(() -> new ResourceNotFoundException("Tenant ID not found"));

    user.setTenant(tenant);
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setPassword(dto.getPassword()); // TODO: hash

    User saved = userRepository.save(user);

    return mapToDTO(saved);
  }

  private UserResponse mapToDTO(User user) {
    UserResponse dto = new UserResponse();
    dto.setId(user.getId());
    dto.setTenant_id(user.getTenant().getId());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    return dto;
  }

}
