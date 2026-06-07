package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Re-triggering Verification Emails
 */
public record ResendCodeRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        String email,
        // ACCOUNT_VERIFICATION, LOGIN or PASSWORD_RESET.
        @NotNull OtpPurpose purpose
) {}