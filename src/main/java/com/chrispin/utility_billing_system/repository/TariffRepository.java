package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Tariff;
import com.chrispin.utility_billing_system.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface
TariffRepository extends JpaRepository<Tariff, UUID> {

    /** Highest active tariff version for a meter type that is already effective for the cycle. */
    Optional<Tariff> findFirstByMeterTypeAndActiveTrueAndEffectiveFromLessThanEqualOrderByVersionDesc(
            MeterType meterType, LocalDate onOrBefore);

    Optional<Tariff> findFirstByMeterTypeOrderByVersionDesc(MeterType meterType);

    List<Tariff> findByMeterTypeOrderByVersionDesc(MeterType meterType);
}
