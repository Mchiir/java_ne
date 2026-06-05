package com.chrispin.utility_billing_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrispin.utility_billing_system.entity.OtpToken;
import com.chrispin.utility_billing_system.enums.OtpPurpose;
import com.chrispin.utility_billing_system.exception.BadRequestException;
import com.chrispin.utility_billing_system.repository.OtpTokenRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * One-time-code engine shared by email verification and password reset.
 * (Passwordless OTP login is intentionally not offered — login is email + password.)
 */
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Value("${app.otp.expiry-minutes:15}")
    private int expiryMinutes;

    /** Invalidates outstanding codes for the purpose and issues a fresh one. */
    @Transactional
    public String issue(String email, OtpPurpose purpose) {
        otpTokenRepository.invalidateAllForEmail(email, purpose);
        String code = generateCode();
        otpTokenRepository.save(OtpToken.builder()
                .email(email)
                .code(code)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes))
                .used(false)
                .build());
        return code;
    }

    /** Validates and consumes a code; throws BadRequest if invalid/expired. */
    @Transactional
    public OtpToken consume(String email, String code, OtpPurpose purpose) {
        OtpToken token = otpTokenRepository
                .findTopByEmailAndCodeAndPurposeAndUsedFalseOrderByExpiresAtDesc(email, code, purpose)
                .orElseThrow(() -> new BadRequestException("Invalid or already-used code"));
        if (token.isExpired()) {
            throw new BadRequestException(
                    "Code has expired. Request a new one at /api/auth/code/resend (purpose " + purpose + ").");
        }
        token.setUsed(true);
        otpTokenRepository.save(token);
        return token;
    }

    public int getExpiryMinutes() {
        return expiryMinutes;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}
