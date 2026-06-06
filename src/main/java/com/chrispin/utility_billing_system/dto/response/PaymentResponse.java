package com.chrispin.utility_billing_system.dto.response;


import com.chrispin.utility_billing_system.entity.Payment;
import com.chrispin.utility_billing_system.enums.BillStatus;
import com.chrispin.utility_billing_system.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String billReference,
        BigDecimal amountPaid,
        PaymentMethod paymentMethod,
        LocalDateTime paymentDate,
        String reference,
        BigDecimal billOutstandingBalance,
        BillStatus billStatus
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getBill().getBillReference(), p.getAmountPaid(),
                p.getPaymentMethod(), p.getPaymentDate(), p.getReference(),
                p.getBill().getOutstandingBalance(), p.getBill().getStatus());
    }
}
