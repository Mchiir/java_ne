package rw.utility.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rw.utility.billing.enums.MeterType;

import java.time.LocalDate;

public record MeterRequest(
        @NotBlank String meterNumber,
        @NotNull MeterType meterType,
        LocalDate installationDate,
        @NotNull Long customerId
) {}
