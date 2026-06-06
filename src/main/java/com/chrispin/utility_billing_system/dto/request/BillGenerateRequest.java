package com.chrispin.utility_billing_system.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Generate a bill from a captured meter reading. The billing cycle (month/year),
 * consumption and customer are derived from the reading; the tariff is the active
 * version effective for that cycle.
 */
public record BillGenerateRequest(
        @NotNull UUID meterReadingId,
        /** Optional payment due date; defaults to generation date + 15 days. */
        LocalDate dueDate
) {}
