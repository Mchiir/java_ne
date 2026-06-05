package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);
    List<Notification> findByCustomer_User_EmailOrderByCreatedAtDesc(String email);
    List<Notification> findAllByOrderByCreatedAtDesc();
}
