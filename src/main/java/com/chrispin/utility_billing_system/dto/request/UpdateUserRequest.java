package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * User Profile Updates
 */
public record UpdateUserRequest(
        @Size(max = 100, message = "Full names must not exceed 100 characters.")
        String fullNames,

        @Email(message = "Invalid email format.")
        String email,

        String address,

        @Pattern(
                regexp = "^(\\+?250|0)?7[2389]\\d{7}$",
                message = "Invalid Rwandan phone number. Supports formats like: +25079XXXXXXX, 25079XXXXXXX, or 079XXXXXXX."
        )
        String phoneNumber,

        @Pattern(
                regexp = "^[0-9]{16}$",
                message = "National ID must contain exactly 16 digits"
        )
        String nationalId
) {}
