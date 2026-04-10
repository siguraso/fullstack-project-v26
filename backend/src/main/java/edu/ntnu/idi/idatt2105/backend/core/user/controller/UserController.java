package edu.ntnu.idi.idatt2105.backend.core.user.controller;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserCreateRequest;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserUpdateRequest;
import edu.ntnu.idi.idatt2105.backend.core.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users within a tenant organisation.
 * <p>
 * All operations are restricted to the {@code ADMIN} role and are scoped to
 * the current tenant. Provides endpoints for listing, creating, updating and
 * deactivating users.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns all users belonging to the current tenant.
     *
     * @return a list of {@link UserResponse} objects
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers()));
    }

    /**
     * Retrieves a single user by their identifier.
     *
     * @param id the user identifier
     * @return the {@link UserResponse} for the requested user
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }

    /**
     * Creates a new user in the current tenant.
     *
     * @param dto the user creation request containing name, email and role
     * @return a 201 Created response containing the persisted
     *         {@link UserResponse}
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest dto) {
        return ResponseEntity
                .status(201)
                .body(ApiResponse.ok("User created successfully", userService.createUser(dto)));
    }

    /**
     * Updates a user's profile and role.
     *
     * @param id  identifier of the user to update
     * @param dto the updated user details
     * @return the updated {@link UserResponse}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest dto) {
        return ResponseEntity.ok(ApiResponse.ok("User updated successfully", userService.updateUser(id, dto)));
    }

    /**
     * Deactivates a user, preventing further login without removing the
     * account.
     *
     * @param id identifier of the user to deactivate
     * @return a 200 OK response with a confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User deactivated successfully"));
    }
}