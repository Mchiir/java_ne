package rw.utility.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.billing.entity.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBill_Id(Long billId);
    List<Payment> findByBill_Customer_Id(Long customerId);
    List<Payment> findByBill_Customer_User_Email(String email);
}
