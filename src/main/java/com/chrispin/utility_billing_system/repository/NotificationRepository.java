package rw.utility.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.billing.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);
    List<Notification> findByCustomer_User_EmailOrderByCreatedAtDesc(String email);
    List<Notification> findAllByOrderByCreatedAtDesc();
}
