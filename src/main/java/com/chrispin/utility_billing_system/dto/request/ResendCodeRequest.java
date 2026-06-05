package rw.utility.billing.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.utility.billing.enums.OtpPurpose;

public record ResendCodeRequest(
        @NotBlank @Email String email,
        /** ACCOUNT_VERIFICATION, LOGIN or PASSWORD_RESET. */
        @NotNull OtpPurpose purpose
) {}
