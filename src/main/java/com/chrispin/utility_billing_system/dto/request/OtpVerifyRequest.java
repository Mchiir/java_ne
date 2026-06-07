package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Base OTP Token Authentication
 */
public record OtpVerifyRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        String email,

        @NotBlank(message = "OTP verification code is required.")
        @Pattern(regexp = "^\\d{6}$", message = "OTP code must be exactly 6 digits.")
        String code
) {}
