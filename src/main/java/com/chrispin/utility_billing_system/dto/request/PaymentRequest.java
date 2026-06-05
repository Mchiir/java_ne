package rw.utility.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import rw.utility.billing.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank String billReference,
        @NotNull @Positive BigDecimal amountPaid,
        @NotNull PaymentMethod paymentMethod,
        String reference
) {}
