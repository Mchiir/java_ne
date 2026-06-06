package com.chrispin.utility_billing_system.dto.response;

import com.chrispin.utility_billing_system.entity.Notification;
import com.chrispin.utility_billing_system.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID customerId,
        String customerNames,
        UUID billId,
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
