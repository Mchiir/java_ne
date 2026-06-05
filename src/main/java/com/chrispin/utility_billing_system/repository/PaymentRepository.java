package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBill_Id(Long billId);
    List<Payment> findByBill_Customer_Id(Long customerId);
    List<Payment> findByBill_Customer_User_Email(String email);
}
