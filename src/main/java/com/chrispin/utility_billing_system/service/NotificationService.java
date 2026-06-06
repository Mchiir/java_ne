package com.chrispin.utility_billing_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrispin.utility_billing_system.dto.response.NotificationResponse;
import com.chrispin.utility_billing_system.repository.NotificationRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findByCustomer(UUID customerId) {
        return notificationRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId).stream()
                .map(NotificationResponse::from).toList();
    }

    /** Notifications for the authenticated customer account. */
    @Transactional(readOnly = true)
    public List<NotificationResponse> findMine(String email) {
        return notificationRepository.findByCustomer_EmailOrderByCreatedAtDesc(email).stream()
                .map(NotificationResponse::from).toList();
    }
}
