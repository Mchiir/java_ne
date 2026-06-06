package com.chrispin.utility_billing_system.dto.response;



import com.chrispin.utility_billing_system.entity.User;
import com.chrispin.utility_billing_system.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullNames,
        String email,
        String address,
        String phoneNumber,
        String nationalId,
        Status status,
        List<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getFullNames(), u.getEmail(), u.getAddress(), u.getPhoneNumber(), u.getNationalId(), u.getStatus(),
                u.getRoles().stream().map(r -> r.getName().name()).toList(), u.getCreatedAt(), u.getUpdatedAt());
    }
}
