package com.chrispin.utility_billing_system.dto.response;



import com.chrispin.utility_billing_system.entity.User;
import com.chrispin.utility_billing_system.enums.Status;

import java.util.List;

public record UserResponse(
        Long id,
        String fullNames,
        String email,
        String phoneNumber,
        Status status,
        List<String> roles
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getFullNames(), u.getEmail(), u.getPhoneNumber(), u.getStatus(),
                u.getRoles().stream().map(r -> r.getName().name()).toList());
    }
}
