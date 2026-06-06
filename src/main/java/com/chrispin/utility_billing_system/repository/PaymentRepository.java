package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByBill_Id(UUID billId);
    List<Payment> findByBill_Customer_Id(UUID customerId);
    List<Payment> findByBill_Customer_Email(String email);
}
