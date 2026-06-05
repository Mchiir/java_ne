package rw.utility.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.utility.billing.entity.OtpToken;
import rw.utility.billing.enums.OtpPurpose;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByEmailAndCodeAndPurposeAndUsedFalseOrderByExpiresAtDesc(
            String email, String code, OtpPurpose purpose);

    @Modifying
    @Query("update OtpToken o set o.used = true where o.email = :email and o.purpose = :purpose and o.used = false")
    void invalidateAllForEmail(@Param("email") String email, @Param("purpose") OtpPurpose purpose);
}
