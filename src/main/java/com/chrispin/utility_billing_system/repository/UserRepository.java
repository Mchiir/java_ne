package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.User;
import com.chrispin.utility_billing_system.enums.ERole;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNationalId(String nationalId);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        JOIN u.roles r
        WHERE r.name = :role
    """)
    List<User> findByRole(@Param("role") ERole role);
}
