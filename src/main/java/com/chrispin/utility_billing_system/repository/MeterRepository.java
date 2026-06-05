package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeterRepository extends JpaRepository<Meter, Long> {
    boolean existsByMeterNumber(String meterNumber);
    List<Meter> findByCustomer_Id(Long customerId);
}
