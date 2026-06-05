package rw.utility.billing.dto.response;

import rw.utility.billing.entity.Notification;
import rw.utility.billing.enums.NotificationStatus;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long customerId,
        String customerNames,
        Long billId,
        String message,
        String type,
        NotificationStatus status,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getCustomer() != null ? n.getCustomer().getId() : null,
                n.getCustomer() != null ? n.getCustomer().getFullNames() : null,
                n.getBill() != null ? n.getBill().getId() : null,
                n.getMessage(), n.getType(), n.getStatus(), n.getCreatedAt());
    }
}
