package com.chrispin.utility_billing_system.dto.response;


import com.chrispin.utility_billing_system.entity.MeterReading;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MeterReadingResponse(
        UUID id,
        UUID meterId,
        String meterNumber,
        BigDecimal previousReading,
        BigDecimal currentReading,
        BigDecimal consumption,
        LocalDate readingDate,
        Integer readingMonth,
        Integer readingYear
) {
    public static MeterReadingResponse from(MeterReading r) {
        return new MeterReadingResponse(r.getId(), r.getMeter().getId(), r.getMeter().getMeterNumber(),
                r.getPreviousReading(), r.getCurrentReading(), r.getConsumption(),
                r.getReadingDate(), r.getReadingMonth(), r.getReadingYear());
    }
}
