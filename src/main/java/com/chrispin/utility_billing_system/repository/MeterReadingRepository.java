package rw.utility.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.utility.billing.entity.MeterReading;

import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    boolean existsByMeter_IdAndReadingMonthAndReadingYear(Long meterId, Integer month, Integer year);

    Optional<MeterReading> findByMeter_IdAndReadingMonthAndReadingYear(Long meterId, Integer month, Integer year);

    /** Most recent reading for a meter (used to default the previous reading). */
    Optional<MeterReading> findTopByMeter_IdOrderByReadingYearDescReadingMonthDesc(Long meterId);

    List<MeterReading> findByMeter_Id(Long meterId);
}
