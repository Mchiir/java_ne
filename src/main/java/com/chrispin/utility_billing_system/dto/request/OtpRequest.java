package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Token Verification request
 * @param email
 */
public record OtpRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        String email
) {}
