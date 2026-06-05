package rw.utility.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.billing.entity.Tariff;
import rw.utility.billing.enums.MeterType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    /** Highest active tariff version for a meter type that is already effective for the cycle. */
    Optional<Tariff> findFirstByMeterTypeAndActiveTrueAndEffectiveFromLessThanEqualOrderByVersionDesc(
            MeterType meterType, LocalDate onOrBefore);

    Optional<Tariff> findFirstByMeterTypeOrderByVersionDesc(MeterType meterType);

    List<Tariff> findByMeterTypeOrderByVersionDesc(MeterType meterType);
}
