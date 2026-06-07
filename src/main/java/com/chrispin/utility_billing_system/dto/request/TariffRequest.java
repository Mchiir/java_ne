package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.MeterType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Configuration of a New Tariff Plan
 */
public record TariffRequest(
        @NotNull MeterType meterType,

        @NotNull(message = "Consumption rate is required.")
        @PositiveOrZero(message = "Consumption rate must be zero or positive.")
        BigDecimal consumptionRate,

        @NotNull(message = "Fixed service charge is required.")
        @PositiveOrZero(message = "Fixed service charge must be zero or positive.")
        BigDecimal fixedServiceCharge,

        @NotNull(message = "VAT rate is required.")
        @PositiveOrZero(message = "VAT rate must be zero or positive.")
        BigDecimal vatRate,

        @NotNull(message = "Penalty rate is required.")
        @PositiveOrZero(message = "Penalty rate must be zero or positive.")
        BigDecimal penaltyRate,

        // First billing cycle this tariff applies to. Must be a future-facing date.
        @NotNull(message = "Effective date is required.")
        @Future(message = "Effective date must be a future-facing billing cycle date.")
        LocalDate effectiveFrom
) {}