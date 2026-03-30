package edu.ntnu.idi.idatt2105.backend.core.user.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.user.mapper.UserMapper;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.User;
import edu.ntnu.idi.idatt2105.backend.core.user.entity.UserRole;
import edu.ntnu.idi.idatt2105.backend.core.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final TenantRepository tenantRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, TenantRepository tenantRepository, UserMapper userMapper,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getUsersByTenantId(Long tenantId) {
    return userRepository.findAllByTenantId(tenantId)
        .stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User ID not found"));
    return userMapper.toResponse(user);
  }

  public UserResponse createUser(UserCreateRequest dto) {
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new ValidationException("Email already exists: " + dto.getEmail());
    }

    if (userRepository.existsByUsername(dto.getUsername())) {
      throw new ValidationException("Username already exists: " + dto.getUsername());
    }

    User user = new User();
    Tenant tenant = tenantRepository.findById(dto.getTenantId())
        .orElseThrow(() -> new ResourceNotFoundException("Tenant ID not found"));

    user.setTenant(tenant);
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRole(dto.getRole() != null ? dto.getRole() : UserRole.STAFF);
    user.setActive(true);

    User saved = userRepository.save(user);

    return userMapper.toResponse(saved);
  }

  public void deactivateUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User ID not found"));

    user.setActive(false);
    userRepository.save(user);
  }

}
