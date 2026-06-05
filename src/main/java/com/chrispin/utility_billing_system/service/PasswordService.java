package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.billing.dto.request.ChangePasswordRequest;
import rw.utility.billing.dto.request.ForgotPasswordRequest;
import rw.utility.billing.dto.request.ResetPasswordRequest;
import rw.utility.billing.dto.response.MessageResponse;
import rw.utility.billing.entity.User;
import rw.utility.billing.enums.OtpPurpose;
import rw.utility.billing.exception.BadRequestException;
import rw.utility.billing.exception.ResourceNotFoundException;
import rw.utility.billing.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;

    /** Emails a one-time password-reset code. */
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        // Always respond the same way to avoid leaking which emails are registered.
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            String code = otpService.issue(user.getEmail(), OtpPurpose.PASSWORD_RESET);
            emailService.send(user.getEmail(), "Reset your Utility Billing password",
                    "Dear " + user.getFullNames() + ",\n\n"
                            + "Your password reset code is: " + code + "\n"
                            + "It expires in " + otpService.getExpiryMinutes() + " minutes.\n\n"
                            + "If you did not request a reset, please ignore this email.");
        });
        return new MessageResponse(
                "If an account exists for " + request.email() + ", a reset code has been sent.");
    }

    /** Verifies the reset code and sets a new password. */
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        otpService.consume(request.email(), request.code(), OtpPurpose.PASSWORD_RESET);
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new MessageResponse("Password has been reset successfully");
    }

    /** Authenticated self-service password change. */
    @Transactional
    public MessageResponse changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new MessageResponse("Password changed successfully");
    }
}
