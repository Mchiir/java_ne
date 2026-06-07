package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Password Recovery via OTP Code
 */
public record ResetPasswordRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        String email,

        @NotBlank(message = "OTP recovery code is required.")
        @Pattern(regexp = "^\\d{6}$", message = "OTP code must be exactly 6 digits.")
        String code,

        @NotBlank(message = "New password is required.")
        @Size(min = 8, max = 100, message = "New password must be at least 8 characters long.")
        String newPassword
) {}
