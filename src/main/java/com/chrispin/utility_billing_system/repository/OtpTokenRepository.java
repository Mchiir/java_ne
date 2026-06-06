package com.chrispin.utility_billing_system.repository;

import com.chrispin.utility_billing_system.entity.OtpToken;
import com.chrispin.utility_billing_system.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {

    Optional<OtpToken> findTopByEmailAndCodeAndPurposeAndUsedFalseOrderByExpiresAtDesc(
            String email, String code, OtpPurpose purpose);

    @Modifying
    @Query("update OtpToken o set o.used = true where o.email = :email and o.purpose = :purpose and o.used = false")
    void invalidateAllForEmail(@Param("email") String email, @Param("purpose") OtpPurpose purpose);
}
