package com.chrispin.utility_billing_system.service;

import com.chrispin.utility_billing_system.dto.request.*;
import com.chrispin.utility_billing_system.dto.response.JwtResponse;
import com.chrispin.utility_billing_system.dto.response.MessageResponse;
import com.chrispin.utility_billing_system.entity.Role;
import com.chrispin.utility_billing_system.entity.User;
import com.chrispin.utility_billing_system.enums.ERole;
import com.chrispin.utility_billing_system.enums.OtpPurpose;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.exception.BadRequestException;
import com.chrispin.utility_billing_system.exception.DuplicateResourceException;
import com.chrispin.utility_billing_system.exception.ResourceNotFoundException;
import com.chrispin.utility_billing_system.repository.RoleRepository;
import com.chrispin.utility_billing_system.repository.UserRepository;
import com.chrispin.utility_billing_system.security.JwtUtils;
import com.chrispin.utility_billing_system.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;
    private final EmailService emailService;
    private final PasswordService passwordService;

    @Transactional
    public MessageResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already in use: " + request.email());
        }

        // provide role
        Set<Role> roles = new HashSet<>();
        roles.add(getRole(ERole.ROLE_CUSTOMER));

        User user = User.builder()
                .fullNames(request.fullNames())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .status(Status.ACTIVE)
                .emailVerified(false)
                .roles(roles)
                .build();
        userRepository.save(user);

        sendVerificationCode(user);
        return new MessageResponse("User registered. A verification code has been emailed to "
                + user.getEmail() + ". Verify it at /api/auth/verify before logging in.");
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getUsername()));
        requireVerified(user);
        return buildJwt(user, principal);
    }

    /** Confirms the email with the signup code and logs the user in. */
    @Transactional
    public JwtResponse verifyAccount(OtpVerifyRequest request) {
        otpService.consume(request.email(), request.code(), OtpPurpose.ACCOUNT_VERIFICATION);
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));
        user.setEmailVerified(true);
        userRepository.save(user);
        return buildJwt(user, UserDetailsImpl.build(user));
    }

    /**
     * Unified "request a new code" for any flow. Issuing a fresh code invalidates the
     * previous one, so this also covers the case where a code expired before it was used.
     */
    @Transactional
    public MessageResponse resendCode(ResendCodeRequest request) {
        return switch (request.purpose()) {
            case ACCOUNT_VERIFICATION -> resendVerification(new OtpRequest(request.email()));
            case PASSWORD_RESET -> passwordService.forgotPassword(new ForgotPasswordRequest(request.email()));
            case LOGIN -> throw new BadRequestException(
                    "OTP login is not supported. Log in with email + password.");
        };
    }

    /** Re-sends the email-verification code. */
    @Transactional
    public MessageResponse resendVerification(OtpRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));
        if (user.isEmailVerified()) {
            return new MessageResponse("Email is already verified.");
        }
        sendVerificationCode(user);
        return new MessageResponse("A new verification code has been emailed to " + user.getEmail());
    }

    private void sendVerificationCode(User user) {
        String code = otpService.issue(user.getEmail(), OtpPurpose.ACCOUNT_VERIFICATION);
        emailService.send(user.getEmail(), "Verify your Utility Billing account",
                "Dear " + user.getFullNames() + ",\n\n"
                        + "Welcome! Your email verification code is: " + code + "\n"
                        + "It expires in " + otpService.getExpiryMinutes() + " minutes.\n\n"
                        + "Enter it at /api/auth/verify to activate your account.");
    }

    private void requireVerified(User user) {
        if (!user.isEmailVerified()) {
            throw new BadRequestException(
                    "Email not verified. Check your inbox for the verification code, "
                            + "or request a new one at /api/auth/verify/resend.");
        }
    }

    private JwtResponse buildJwt(User user, UserDetailsImpl principal) {
        String token = jwtUtils.generateToken(principal);
        return new JwtResponse(token, user.getId(), user.getEmail(), user.getFullNames(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    private Role getRole(ERole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new BadRequestException("Role not configured: " + name));
    }

    private ERole parseRole(String raw) {
        String normalized = raw.trim().toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        try {
            return ERole.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown role: " + raw);
        }
    }
}
