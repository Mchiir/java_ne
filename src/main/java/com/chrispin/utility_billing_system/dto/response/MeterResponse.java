package rw.utility.billing.dto.response;

import rw.utility.billing.entity.Meter;
import rw.utility.billing.enums.MeterType;
import rw.utility.billing.enums.Status;

import java.time.LocalDate;

public record MeterResponse(
        Long id,
        String meterNumber,
        MeterType meterType,
        LocalDate installationDate,
        Status status,
        Long customerId,
        String customerNames
) {
    public static MeterResponse from(Meter m) {
        return new MeterResponse(m.getId(), m.getMeterNumber(), m.getMeterType(),
                m.getInstallationDate(), m.getStatus(),
                m.getCustomer().getId(), m.getCustomer().getFullNames());
    }
}
