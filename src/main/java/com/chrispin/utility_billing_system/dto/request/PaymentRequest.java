package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Posting a Payment (e.g., via MoMo / Bank API)
 */
public record PaymentRequest(
        @NotBlank(message = "Bill reference is required.")
        String billReference,

        @NotNull(message = "Amount paid is required.")
        @Positive(message = "Amount paid must be greater than zero.")
        BigDecimal amountPaid,

        @NotNull PaymentMethod paymentMethod,
        @NotBlank(message = "External transaction reference is required.")
        String reference
) {}
