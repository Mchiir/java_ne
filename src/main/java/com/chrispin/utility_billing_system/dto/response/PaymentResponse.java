package rw.utility.billing.dto.response;

import rw.utility.billing.entity.Payment;
import rw.utility.billing.enums.BillStatus;
import rw.utility.billing.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
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
