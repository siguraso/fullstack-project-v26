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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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
    Long tenantId = TenantContext.getCurrentOrg();
    List<UserResponse> users = userRepository.findAllByTenantId(tenantId)
        .stream()
        .map(userMapper::toResponse)
        .toList();
    log.debug("Fetched {} users for tenantId={}", users.size(), tenantId);
    return users;
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    log.debug("Fetching user by id={} for current tenant", id);
    User user = getCurrentTenantUser(id);
    return userMapper.toResponse(user);
  }

  public UserResponse updateUser(Long id, UserUpdateRequest dto) {
    log.info("Updating user id={} for current tenant", id);
    User user = getCurrentTenantUser(id);

    if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
      log.warn("Rejecting update for user id={} due to duplicate email", id);
      throw new ValidationException("Email already exists: " + dto.getEmail());
    }

    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.setRole(dto.getRole());
    User saved = userRepository.save(user);
    log.info("Updated user id={} role={}", saved.getId(), saved.getRole());

    return userMapper.toResponse(saved);
  }

  public UserResponse createUser(UserCreateRequest dto) {
    Long currentTenantId = TenantContext.getCurrentOrg();
    log.info("Creating user in tenantId={} with requested role={}", currentTenantId, dto.getRole());

    if (userRepository.existsByEmail(dto.getEmail())) {
      log.warn("Rejecting user creation in tenantId={} due to duplicate email", currentTenantId);
      throw new ValidationException("Email already exists: " + dto.getEmail());
    }

    if (userRepository.existsByUsername(dto.getUsername())) {
      log.warn("Rejecting user creation in tenantId={} due to duplicate username", currentTenantId);
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
    log.info("Created user id={} in tenantId={}", saved.getId(), currentTenantId);

    return userMapper.toResponse(saved);
  }

  public void deactivateUser(Long id) {
    log.info("Deactivating user id={} for current tenant", id);
    User user = getCurrentTenantUser(id);

    user.setActive(false);
    userRepository.save(user);
    log.info("Deactivated user id={}", id);
  }

  private User getCurrentTenantUser(Long id) {
    Long tenantId = TenantContext.getCurrentOrg();

    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User ID not found"));

    if (!user.getTenant().getId().equals(tenantId)) {
      log.warn("Access denied to user id={} for tenantId={}", id, tenantId);
      throw new UnauthorizedException("You do not have access to this user");
    }

    return user;
  }

}
