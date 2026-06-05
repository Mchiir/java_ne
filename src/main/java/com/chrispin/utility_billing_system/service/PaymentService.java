package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.billing.dto.request.PaymentRequest;
import rw.utility.billing.dto.response.PaymentResponse;
import rw.utility.billing.entity.Bill;
import rw.utility.billing.entity.Payment;
import rw.utility.billing.enums.BillStatus;
import rw.utility.billing.exception.BadRequestException;
import rw.utility.billing.exception.ResourceNotFoundException;
import rw.utility.billing.repository.BillRepository;
import rw.utility.billing.repository.PaymentRepository;
import rw.utility.billing.util.NotificationMessages;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    public List<PaymentResponse> findByBill(Long billId) {
        return paymentRepository.findByBill_Id(billId).stream().map(PaymentResponse::from).toList();
    }

    /** Payment history for the authenticated customer account. */
    @Transactional(readOnly = true)
    public List<PaymentResponse> findMine(String email) {
        return paymentRepository.findByBill_Customer_User_Email(email).stream()
                .map(PaymentResponse::from).toList();
    }
}
