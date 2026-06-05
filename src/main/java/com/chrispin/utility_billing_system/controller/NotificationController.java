package com.chrispin.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.utility.billing.dto.response.NotificationResponse;
import rw.utility.billing.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Customer billing/payment notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List all notifications")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    public List<NotificationResponse> findAll() {
        return notificationService.findAll();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List notifications for a customer")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','OPERATOR')")
    public List<NotificationResponse> findByCustomer(@PathVariable Long customerId) {
        return notificationService.findByCustomer(customerId);
    }

    @GetMapping("/me")
    @Operation(summary = "View my own notifications (ROLE_CUSTOMER)")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<NotificationResponse> findMine(Authentication authentication) {
        return notificationService.findMine(authentication.getName());
    }
}
