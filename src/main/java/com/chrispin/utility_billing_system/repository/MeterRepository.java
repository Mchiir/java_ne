package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeterRepository extends JpaRepository<Meter, UUID> {
    boolean existsByMeterNumber(String meterNumber);
    List<Meter> findByCustomer_Id(UUID customerId);
}
