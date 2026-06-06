package com.chrispin.utility_billing_system.dto.response;

import java.util.List;
import java.util.UUID;

public record JwtResponse(
        String token,
        String type,
        UUID id,
        String email,
        String fullNames,
        List<String> roles
) {
    public JwtResponse(String token, UUID id, String email, String fullNames, List<String> roles) {
        this(token, "Bearer", id, email, fullNames, roles);
    }
}
