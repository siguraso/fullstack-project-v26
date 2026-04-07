package edu.ntnu.idi.idatt2105.backend.core.user.service;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import java.util.List;

import edu.ntnu.idi.idatt2105.backend.common.exception.ResourceNotFoundException;
import edu.ntnu.idi.idatt2105.backend.common.exception.ValidationException;
import edu.ntnu.idi.idatt2105.backend.core.tenant.context.TenantContext;
import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;
import edu.ntnu.idi.idatt2105.backend.core.tenant.repository.TenantRepository;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserUpdateRequest;
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
    return userRepository.findAllByTenantId(TenantContext.getCurrentOrg())
        .stream()
        .map(userMapper::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    User user = getCurrentTenantUser(id);
    return userMapper.toResponse(user);
  }

  public UserResponse updateUser(Long id, UserUpdateRequest dto) {
    User user = getCurrentTenantUser(id);

    if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
      throw new ValidationException("Email already exists: " + dto.getEmail());
    }

    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.setRole(dto.getRole());
    User saved = userRepository.save(user);

    return userMapper.toResponse(saved);
  }

  public UserResponse createUser(UserCreateRequest dto) {
    Long currentTenantId = TenantContext.getCurrentOrg();

    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new ValidationException("Email already exists: " + dto.getEmail());
    }

    if (userRepository.existsByUsername(dto.getUsername())) {
      throw new ValidationException("Username already exists: " + dto.getUsername());
    }

    User user = new User();
    Tenant tenant = tenantRepository.findById(currentTenantId)
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
    User user = getCurrentTenantUser(id);

    user.setActive(false);
    userRepository.save(user);
  }

  private User getCurrentTenantUser(Long id) {
    Long tenantId = TenantContext.getCurrentOrg();

    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User ID not found"));

    if (!user.getTenant().getId().equals(tenantId)) {
      throw new UnauthorizedException("You do not have access to this user");
    }

    return user;
  }

}
