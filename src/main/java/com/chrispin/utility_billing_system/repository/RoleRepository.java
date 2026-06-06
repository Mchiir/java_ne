package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.Role;
import com.chrispin.utility_billing_system.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(ERole name);
}
