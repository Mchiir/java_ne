package rw.utility.billing.dto.response;

import rw.utility.billing.entity.Bill;
import rw.utility.billing.enums.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillResponse(
        Long id,
        String billReference,
        Long customerId,
        String customerNames,
        Long meterId,
        String meterNumber,
        Integer billingMonth,
        Integer billingYear,
        BigDecimal consumption,
        BigDecimal energyAmount,
        BigDecimal serviceCharge,
        BigDecimal taxAmount,
        BigDecimal penaltyAmount,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal outstandingBalance,
        BillStatus status,
        LocalDate generatedDate,
        LocalDate dueDate
) {
    public static BillResponse from(Bill b) {
        return new BillResponse(
                b.getId(), b.getBillReference(),
                b.getCustomer().getId(), b.getCustomer().getFullNames(),
                b.getMeter().getId(), b.getMeter().getMeterNumber(),
                b.getBillingMonth(), b.getBillingYear(), b.getConsumption(),
                b.getEnergyAmount(), b.getServiceCharge(), b.getTaxAmount(),
                b.getPenaltyAmount(), b.getTotalAmount(), b.getAmountPaid(),
                b.getOutstandingBalance(), b.getStatus(),
                b.getGeneratedDate(), b.getDueDate());
    }
}
