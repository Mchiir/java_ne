package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.billing.dto.response.NotificationResponse;
import rw.utility.billing.repository.NotificationRepository;

import java.util.List;

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
    public List<NotificationResponse> findByCustomer(Long customerId) {
        return notificationRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId).stream()
                .map(NotificationResponse::from).toList();
    }

    /** Notifications for the authenticated customer account. */
    @Transactional(readOnly = true)
    public List<NotificationResponse> findMine(String email) {
        return notificationRepository.findByCustomer_User_EmailOrderByCreatedAtDesc(email).stream()
                .map(NotificationResponse::from).toList();
    }
}
