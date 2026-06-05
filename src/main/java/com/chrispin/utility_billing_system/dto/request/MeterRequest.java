package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MeterRequest(
        @NotBlank String meterNumber,
        @NotNull MeterType meterType,
        LocalDate installationDate,
        @NotNull Long customerId
) {}
