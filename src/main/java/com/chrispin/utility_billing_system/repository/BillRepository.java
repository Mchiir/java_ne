package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends JpaRepository<Bill, UUID> {
    boolean existsByMeterReading_Id(UUID meterReadingId);
    Optional<Bill> findByBillReference(String billReference);
    List<Bill> findByCustomer_Id(UUID customerId);
    List<Bill> findByCustomer_Email(String email);
}