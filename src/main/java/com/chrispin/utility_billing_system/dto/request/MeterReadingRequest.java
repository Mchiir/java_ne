package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MeterReadingRequest(
        @NotNull Long meterId,
        /** Optional: if omitted, defaults to the meter's last current reading (or 0). */
        @PositiveOrZero BigDecimal previousReading,
        @NotNull @PositiveOrZero BigDecimal currentReading,
        @NotNull LocalDate readingDate
) {}
