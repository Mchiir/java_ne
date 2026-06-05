package com.chrispin.utility_billing_system.dto.request;

import com.chrispin.utility_billing_system.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank String billReference,
        @NotNull @Positive BigDecimal amountPaid,
        @NotNull PaymentMethod paymentMethod,
        String reference
) {}
