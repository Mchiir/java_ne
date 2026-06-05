package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Role;
import com.chrispin.utility_billing_system.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
