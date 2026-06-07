package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Capturing Meter Readings
 */
public record MeterReadingRequest(
        @NotNull UUID meterId,
        /** Optional: if omitted, defaults to the meter's last current reading (or 0). */
        @PositiveOrZero(message = "Previous reading cannot be a negative value.")
        BigDecimal previousReading,
        @NotNull(message = "Current reading is required.")
        @PositiveOrZero(message = "Current reading cannot be a negative value.")
        BigDecimal currentReading,

        @NotNull(message = "Reading capture date is required.")
        @PastOrPresent(message = "Reading date cannot look into the future.")
        LocalDate readingDate
) {}
