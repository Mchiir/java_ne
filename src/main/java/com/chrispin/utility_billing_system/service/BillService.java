package com.chrispin.utility_billing_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrispin.utility_billing_system.dto.request.BillGenerateRequest;
import com.chrispin.utility_billing_system.dto.response.BillResponse;
import com.chrispin.utility_billing_system.entity.*;
import com.chrispin.utility_billing_system.enums.BillStatus;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.exception.BadRequestException;
import com.chrispin.utility_billing_system.exception.ResourceNotFoundException;
import com.chrispin.utility_billing_system.repository.BillRepository;
import com.chrispin.utility_billing_system.repository.MeterReadingRepository;
import com.chrispin.utility_billing_system.repository.UserRepository;
import com.chrispin.utility_billing_system.util.NotificationMessages;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final MeterReadingRepository readingRepository;
    private final TariffService tariffService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int HUNDRED = 100;

    /**
     * Generates a postpaid bill from a captured meter reading. On insert, a database
     * trigger automatically creates the customer notification (see db/routines.sql).
     */
    @Transactional
    public BillResponse generate(BillGenerateRequest request) {
        MeterReading reading = readingRepository.findById(request.meterReadingId())
                .orElseThrow(() -> new ResourceNotFoundException("MeterReading", "id", request.meterReadingId()));

        if (billRepository.existsByMeterReading_Id(reading.getId())) {
            throw new BadRequestException("A bill has already been generated for this reading (id "
                    + reading.getId() + ")");
        }

        Meter meter = reading.getMeter();
        User customer = meter.getCustomer();

        // Business rule: inactive customers cannot receive bills.
        if (customer.getStatus() != Status.ACTIVE) {
            throw new BadRequestException("Cannot bill an inactive customer: " + customer.getFullNames());
        }

        int month = reading.getReadingMonth();
        int year = reading.getReadingYear();
        LocalDate cycleStart = LocalDate.of(year, month, 1);

        Tariff tariff = tariffService.resolveForCycle(meter.getMeterType(), cycleStart);

        BigDecimal consumption = reading.getConsumption();
        BigDecimal energyAmount = scale(consumption.multiply(tariff.getConsumptionRate()));
        BigDecimal serviceCharge = scale(tariff.getFixedServiceCharge());
        BigDecimal taxable = energyAmount.add(serviceCharge);
        BigDecimal taxAmount = scale(taxable.multiply(tariff.getVatRate())
                .divide(BigDecimal.valueOf(HUNDRED), 4, RoundingMode.HALF_UP));
        BigDecimal totalAmount = scale(energyAmount.add(serviceCharge).add(taxAmount));

        LocalDate generatedDate = LocalDate.now();
        LocalDate dueDate = request.dueDate() != null ? request.dueDate() : generatedDate.plusDays(15);

        Bill bill = Bill.builder()
                .billReference(buildReference(meter.getMeterNumber(), year, month))
                .customer(customer)
                .meter(meter)
                .meterReading(reading)
                .tariff(tariff)
                .billingMonth(month)
                .billingYear(year)
                .consumption(consumption)
                .energyAmount(energyAmount)
                .serviceCharge(serviceCharge)
                .taxAmount(taxAmount)
                .penaltyAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .amountPaid(BigDecimal.ZERO)
                .outstandingBalance(totalAmount)
                .status(BillStatus.PENDING)
                .generatedDate(generatedDate)
                .dueDate(dueDate)
                .build();

        Bill saved = billRepository.save(bill);

        // Email the customer the same message the DB trigger records as a notification.
        emailService.send(customer.getEmail(), "Your utility bill " + saved.getBillReference(),
                NotificationMessages.billProcessed(customer.getFullNames(), month, year, totalAmount));

        return BillResponse.from(saved);
    }

    /** Approval by ADMIN/FINANCE. Only a PENDING bill can be approved. */
    @Transactional
    public BillResponse approve(UUID id, String approverEmail) {
        Bill bill = getBill(id);
        if (bill.getStatus() != BillStatus.PENDING) {
            throw new BadRequestException("Only PENDING bills can be approved; current status: "
                    + bill.getStatus());
        }
        User approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", approverEmail));
        bill.setStatus(BillStatus.APPROVED);
        bill.setApprovedBy(approver);
        return BillResponse.from(billRepository.save(bill));
    }

    @Transactional(readOnly = true)
    public List<BillResponse> findAll() {
        return billRepository.findAll().stream().map(BillResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BillResponse findById(UUID id) {
        return BillResponse.from(getBill(id));
    }

    @Transactional(readOnly = true)
    public List<BillResponse> findByCustomer(UUID customerId) {
        return billRepository.findByCustomer_Id(customerId).stream().map(BillResponse::from).toList();
    }

    /** Bills belonging to the authenticated customer account. */
    @Transactional(readOnly = true)
    public List<BillResponse> findMine(String email) {
        return billRepository.findByCustomer_Email(email).stream().map(BillResponse::from).toList();
    }

    private Bill getBill(UUID id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
    }

    private String buildReference(String meterNumber, int year, int month) {
        return String.format("BILL-%d%02d-%s", year, month, meterNumber);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
