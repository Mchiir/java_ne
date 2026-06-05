package rw.utility.billing.dto.response;

import rw.utility.billing.entity.MeterReading;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MeterReadingResponse(
        Long id,
        Long meterId,
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
