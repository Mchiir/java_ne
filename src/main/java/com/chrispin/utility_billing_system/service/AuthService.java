package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.billing.dto.request.ForgotPasswordRequest;
import rw.utility.billing.dto.request.LoginRequest;
import rw.utility.billing.dto.request.OtpRequest;
import rw.utility.billing.dto.request.OtpVerifyRequest;
import rw.utility.billing.dto.request.ResendCodeRequest;
import rw.utility.billing.dto.request.SignupRequest;
import rw.utility.billing.dto.response.JwtResponse;
import rw.utility.billing.dto.response.MessageResponse;
import rw.utility.billing.entity.Role;
import rw.utility.billing.entity.User;
import rw.utility.billing.enums.ERole;
import rw.utility.billing.enums.OtpPurpose;
import rw.utility.billing.enums.Status;
import rw.utility.billing.exception.BadRequestException;
import rw.utility.billing.exception.DuplicateResourceException;
import rw.utility.billing.exception.ResourceNotFoundException;
import rw.utility.billing.repository.RoleRepository;
import rw.utility.billing.repository.UserRepository;
import rw.utility.billing.security.JwtUtils;
import rw.utility.billing.security.UserDetailsImpl;

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

        Set<Role> roles = new HashSet<>();
        if (request.roles() == null || request.roles().isEmpty()) {
            roles.add(getRole(ERole.ROLE_CUSTOMER));
        } else {
            for (String r : request.roles()) {
                roles.add(getRole(parseRole(r)));
            }
        }

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
