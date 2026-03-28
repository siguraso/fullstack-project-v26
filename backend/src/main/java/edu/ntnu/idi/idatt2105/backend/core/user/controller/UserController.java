package edu.ntnu.idi.idatt2105.backend.core.user.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ntnu.idi.idatt2105.backend.core.user.dto.CreateUser;
import edu.ntnu.idi.idatt2105.backend.core.user.dto.UserResponse;
import edu.ntnu.idi.idatt2105.backend.core.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUser dto) {
        return service.createUser(dto);
    }

}
