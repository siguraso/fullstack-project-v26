package edu.ntnu.idi.idatt2105.backend.core.user.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.CreateUser;
import edu.ntnu.idi.idatt2105.backend.core.user.mapper.UserMapper;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final TenantRepository tenantRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, TenantRepository tenantRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
    this.userMapper = userMapper;
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
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

    return userMapper.toResponse(saved);
  }

}
