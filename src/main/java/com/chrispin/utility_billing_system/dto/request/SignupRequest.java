package rw.utility.billing.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record SignupRequest(
        @NotBlank String fullNames,
        @NotBlank @Email String email,
        String phoneNumber,
        @NotBlank @Size(min = 6, max = 100) String password,
        /** Optional role names, e.g. ["ROLE_OPERATOR"]. Defaults to ROLE_CUSTOMER. */
        Set<String> roles
) {}
