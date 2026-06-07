package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Logged-in User Security Update
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required.")
        @Size(min = 8, max = 100, message = "password must be at least 8 characters long.")
        String currentPassword,

        @NotBlank(message = "New password is required.")
        @Size(min = 8, max = 100, message = "New password must be at least 8 characters long.")
        String newPassword
) {}
