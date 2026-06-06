package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByCustomer_IdOrderByCreatedAtDesc(UUID customerId);
    List<Notification> findByCustomer_EmailOrderByCreatedAtDesc(String email);
    List<Notification> findAllByOrderByCreatedAtDesc();
}
