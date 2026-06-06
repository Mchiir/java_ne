package com.chrispin.utility_billing_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrispin.utility_billing_system.dto.request.PaymentRequest;
import com.chrispin.utility_billing_system.dto.response.PaymentResponse;
import com.chrispin.utility_billing_system.entity.Bill;
import com.chrispin.utility_billing_system.entity.Payment;
import com.chrispin.utility_billing_system.enums.BillStatus;
import com.chrispin.utility_billing_system.exception.BadRequestException;
import com.chrispin.utility_billing_system.exception.ResourceNotFoundException;
import com.chrispin.utility_billing_system.repository.BillRepository;
import com.chrispin.utility_billing_system.repository.PaymentRepository;
import com.chrispin.utility_billing_system.util.NotificationMessages;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final EmailService emailService;

    /**
     * Records a payment against a bill. Supports partial and full payments, recomputes
     * the outstanding balance and marks the bill PAID when it reaches zero. When the
     * bill becomes fully paid, a database trigger notifies the customer.
     */
    @Transactional
    public PaymentResponse record(PaymentRequest request) {
        Bill bill = billRepository.findByBillReference(request.billReference())
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "reference", request.billReference()));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BadRequestException("Bill is already fully paid: " + bill.getBillReference());
        }

        BigDecimal amount = request.amountPaid();
        if (amount.compareTo(bill.getOutstandingBalance()) > 0) {
            throw new BadRequestException(String.format(
                    "Payment (%s) exceeds the outstanding balance (%s)",
                    amount, bill.getOutstandingBalance()));
        }

        // Update balances.
        bill.setAmountPaid(bill.getAmountPaid().add(amount));
        bill.setOutstandingBalance(bill.getTotalAmount().subtract(bill.getAmountPaid()));
        boolean fullyPaid = bill.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0;
        bill.setStatus(fullyPaid ? BillStatus.PAID : BillStatus.PARTIALLY_PAID);
        billRepository.save(bill);

        Payment payment = Payment.builder()
                .bill(bill)
                .amountPaid(amount)
                .paymentMethod(request.paymentMethod())
                .paymentDate(LocalDateTime.now())
                .reference(request.reference())
                .build();
        Payment saved = paymentRepository.save(payment);

        // On full settlement, email the customer (mirrors the DB full-payment trigger).
        if (fullyPaid) {
            emailService.send(bill.getCustomer().getEmail(),
                    "Payment received for bill " + bill.getBillReference(),
                    NotificationMessages.paymentCompleted(
                            bill.getCustomer().getFullNames(), bill.getBillReference(), bill.getTotalAmount()));
        }

        return PaymentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll().stream().map(PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByBill(UUID billId) {
        return paymentRepository.findByBill_Id(billId).stream().map(PaymentResponse::from).toList();
    }

    /** Payment history for the authenticated customer account. */
    @Transactional(readOnly = true)
    public List<PaymentResponse> findMine(String email) {
        return paymentRepository.findByBill_Customer_Email(email).stream()
                .map(PaymentResponse::from).toList();
    }
}
