package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    boolean existsByMeter_IdAndReadingMonthAndReadingYear(Long meterId, Integer month, Integer year);

    Optional<MeterReading> findByMeter_IdAndReadingMonthAndReadingYear(Long meterId, Integer month, Integer year);

    /** Most recent reading for a meter (used to default the previous reading). */
    Optional<MeterReading> findTopByMeter_IdOrderByReadingYearDescReadingMonthDesc(Long meterId);

    List<MeterReading> findByMeter_Id(Long meterId);
}
