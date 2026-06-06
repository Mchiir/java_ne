package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record MeterRequest(
        @NotBlank String meterNumber,
        @NotNull MeterType meterType,
        LocalDate installationDate,
        @NotNull UUID customerId
) {}
