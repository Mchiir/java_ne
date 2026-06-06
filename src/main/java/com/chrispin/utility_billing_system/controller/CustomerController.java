package com.chrispin.utility_billing_system.controller;

import com.chrispin.utility_billing_system.dto.request.UpdateUserRequest;
import com.chrispin.utility_billing_system.dto.request.UserRequest;
import com.chrispin.utility_billing_system.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.service.CustomerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer registration and management")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Register a new customer")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    @GetMapping
    @Operation(summary = "List all customers")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public List<UserResponse> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by id")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public UserResponse findById(@PathVariable UUID id) {
        return customerService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return customerService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate / deactivate a customer")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public UserResponse updateStatus(@PathVariable UUID id, @RequestParam Status status) {
        return customerService.updateStatus(id, status);
    }
}
