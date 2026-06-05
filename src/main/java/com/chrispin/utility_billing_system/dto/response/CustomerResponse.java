package com.chrispin.utility_billing_system.dto.response;


import com.chrispin.utility_billing_system.entity.Customer;
import com.chrispin.utility_billing_system.enums.Status;

public record CustomerResponse(
        Long id,
        String fullNames,
        String nationalId,
        String email,
        String phoneNumber,
        String address,
        Status status
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getFullNames(), c.getNationalId(),
                c.getEmail(), c.getPhoneNumber(), c.getAddress(), c.getStatus());
    }
}
