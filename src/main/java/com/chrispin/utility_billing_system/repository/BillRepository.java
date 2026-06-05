package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    boolean existsByMeterReading_Id(Long meterReadingId);
    Optional<Bill> findByBillReference(String billReference);
    List<Bill> findByCustomer_Id(Long customerId);
    List<Bill> findByCustomer_User_Email(String email);
}
