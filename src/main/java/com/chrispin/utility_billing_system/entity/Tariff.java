package com.chrispin.utility_billing_system.entity;

import com.chrispin.utility_billing_system.enums.MeterType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A versioned tariff. New tariffs are created as new versions and apply only to
 * billing cycles on/after {@code effectiveFrom}; existing bills keep their tariff.
 */
@Entity
@Table(name = "tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false, length = 20)
    private MeterType meterType;

    /** Price per consumed unit (FRW per m3 / kWh). */
    @Column(name = "consumption_rate", nullable = false, precision = 18, scale = 4)
    private BigDecimal consumptionRate;

    @Column(name = "fixed_service_charge", nullable = false, precision = 18, scale = 2)
    private BigDecimal fixedServiceCharge;

    /** VAT / tax percentage, e.g. 18.00 for 18%. */
    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate;

    /** Late-payment penalty percentage applied to outstanding amount. */
    @Column(name = "penalty_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal penaltyRate;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
