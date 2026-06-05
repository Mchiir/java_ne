package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.MeterType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TariffRequest(
        @NotNull MeterType meterType,
        @NotNull @PositiveOrZero BigDecimal consumptionRate,
        @NotNull @PositiveOrZero BigDecimal fixedServiceCharge,
        @NotNull @PositiveOrZero BigDecimal vatRate,
        @NotNull @PositiveOrZero BigDecimal penaltyRate,
        /** First billing cycle this tariff applies to. Must be a future-facing date. */
        @NotNull LocalDate effectiveFrom
) {}
