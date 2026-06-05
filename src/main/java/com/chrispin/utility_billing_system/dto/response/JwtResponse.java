package rw.utility.billing.dto.response;

import java.util.List;

public record JwtResponse(
        String token,
        String type,
        Long id,
        String email,
        String fullNames,
        List<String> roles
) {
    public JwtResponse(String token, Long id, String email, String fullNames, List<String> roles) {
        this(token, "Bearer", id, email, fullNames, roles);
    }
}
