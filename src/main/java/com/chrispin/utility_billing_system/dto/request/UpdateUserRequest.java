package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
            @NotBlank String fullNames
            ,@NotBlank @Email String email
            ,@NotBlank String address
            ,@NotBlank String phoneNumber
            ,@NotBlank String nationalId
    ) {}
