package com.chrispin.utility_billing_system.dto.response;

import com.chrispin.utility_billing_system.entity.Bill;
import com.chrispin.utility_billing_system.enums.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BillResponse(
        UUID id,
        String billReference,
        UUID customerId,
        String customerNames,
        UUID meterId,
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
