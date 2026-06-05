package rw.utility.billing.dto.response;

import rw.utility.billing.entity.Tariff;
import rw.utility.billing.enums.MeterType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TariffResponse(
        Long id,
        Integer version,
        MeterType meterType,
        BigDecimal consumptionRate,
        BigDecimal fixedServiceCharge,
        BigDecimal vatRate,
        BigDecimal penaltyRate,
        LocalDate effectiveFrom,
        Boolean active
) {
    public static TariffResponse from(Tariff t) {
        return new TariffResponse(t.getId(), t.getVersion(), t.getMeterType(),
                t.getConsumptionRate(), t.getFixedServiceCharge(), t.getVatRate(),
                t.getPenaltyRate(), t.getEffectiveFrom(), t.getActive());
    }
}
