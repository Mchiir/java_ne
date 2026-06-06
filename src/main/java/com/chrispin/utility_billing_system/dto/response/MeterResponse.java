package com.chrispin.utility_billing_system.dto.response;



import com.chrispin.utility_billing_system.entity.Meter;
import com.chrispin.utility_billing_system.enums.MeterType;
import com.chrispin.utility_billing_system.enums.Status;

import java.time.LocalDate;
import java.util.UUID;

public record MeterResponse(
        UUID id,
        String meterNumber,
        MeterType meterType,
        LocalDate installationDate,
        Status status,
        UUID customerId,
        String customerNames
) {
    public static MeterResponse from(Meter m) {
        return new MeterResponse(m.getId(), m.getMeterNumber(), m.getMeterType(),
                m.getInstallationDate(), m.getStatus(),
                m.getCustomer().getId(), m.getCustomer().getFullNames());
    }
}
