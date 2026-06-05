package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByNationalId(String nationalId);
    Optional<Customer> findByNationalId(String nationalId);
    Optional<Customer> findByUser_Email(String email);
}
