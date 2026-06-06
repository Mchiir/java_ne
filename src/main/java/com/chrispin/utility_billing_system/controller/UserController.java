package com.chrispin.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chrispin.utility_billing_system.dto.response.UserResponse;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management (ROLE_ADMIN)")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id")
    public UserResponse findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate / deactivate a user")
    public UserResponse updateStatus(@PathVariable UUID id, @RequestParam Status status) {
        return userService.updateStatus(id, status);
    }
}
