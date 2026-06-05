package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResendCodeRequest(
        @NotBlank @Email String email,
        /** ACCOUNT_VERIFICATION, LOGIN or PASSWORD_RESET. */
        @NotNull OtpPurpose purpose
) {}
