package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String fullNames,
        @NotBlank String nationalId,
        @Email String email,
        String phoneNumber,
        String address
) {}
