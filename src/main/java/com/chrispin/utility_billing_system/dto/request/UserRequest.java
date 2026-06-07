package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * User Account Creation
 */
public record UserRequest(
        @NotBlank(message = "Full names are required.")
        @Size(max = 100, message = "Full names must not exceed 100 characters.")
        String fullNames,

        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        String email,

        @NotBlank(message = "Address is required.")
        String address,

        @NotBlank(message = "Phone number is required.")
        @Pattern(
                regexp = "^(\\+?250|0)?7[2389]\\d{7}$",
                message = "Invalid Rwandan phone number. Supports formats like: +25079XXXXXXX, 25079XXXXXXX, or 079XXXXXXX."
        )
        String phoneNumber,

        @NotBlank(message = "National ID is required.")
        @Pattern(
                regexp = "^[0-9]{16}$",
                message = "National ID must contain exactly 16 digits"
        )
        String nationalId,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
        String password
//        /** Optional role names, e.g. ["ROLE_OPERATOR"]. Defaults to ROLE_CUSTOMER. */
//        ,Set<String> roles
) {}