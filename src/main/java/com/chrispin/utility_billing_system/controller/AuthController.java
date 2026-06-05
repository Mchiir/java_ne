package com.chrispin.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.utility.billing.dto.request.ChangePasswordRequest;
import rw.utility.billing.dto.request.ForgotPasswordRequest;
import rw.utility.billing.dto.request.LoginRequest;
import rw.utility.billing.dto.request.OtpRequest;
import rw.utility.billing.dto.request.OtpVerifyRequest;
import rw.utility.billing.dto.request.ResendCodeRequest;
import rw.utility.billing.dto.request.ResetPasswordRequest;
import rw.utility.billing.dto.request.SignupRequest;
import rw.utility.billing.dto.response.JwtResponse;
import rw.utility.billing.dto.response.MessageResponse;
import rw.utility.billing.service.AuthService;
import rw.utility.billing.service.PasswordService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String SIGNUP = "Auth 1 - Sign-up & Verification";
    private static final String LOGIN = "Auth 2 - Login";
    private static final String PASSWORD = "Auth 3 - Password";

    private final AuthService authService;
    private final PasswordService passwordService;

    // ----- Sign-up & Verification -----

    @PostMapping("/signup")
    @Tag(name = SIGNUP, description = "Register and confirm a new account via emailed code")
    @Operation(summary = "Register a new user (emails a verification code)")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/verify")
    @Tag(name = SIGNUP)
    @Operation(summary = "Verify the email with the signup code (returns a JWT)")
    public ResponseEntity<JwtResponse> verify(@Valid @RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyAccount(request));
    }

    @PostMapping("/verify/resend")
    @Tag(name = SIGNUP)
    @Operation(summary = "Resend the email-verification code")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.resendVerification(request));
    }

    @PostMapping("/code/resend")
    @Tag(name = SIGNUP)
    @Operation(summary = "Request a fresh code for any flow "
            + "(purpose = ACCOUNT_VERIFICATION | PASSWORD_RESET); invalidates the previous code")
    public ResponseEntity<MessageResponse> resendCode(@Valid @RequestBody ResendCodeRequest request) {
        return ResponseEntity.ok(authService.resendCode(request));
    }

    // ----- Login (email + password) -----

    @PostMapping("/login")
    @Tag(name = LOGIN, description = "Email + password login")
    @Operation(summary = "Authenticate with email + password and obtain a JWT (requires verified email)")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ----- Password (forgot / reset / change) -----

    @PostMapping("/password/forgot")
    @Tag(name = PASSWORD, description = "Forgot, reset and change password")
    @Operation(summary = "Request a password-reset code by email")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordService.forgotPassword(request));
    }

    @PostMapping("/password/reset")
    @Tag(name = PASSWORD)
    @Operation(summary = "Reset password using the emailed code")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordService.resetPassword(request));
    }

    @PostMapping("/password/change")
    @Tag(name = PASSWORD)
    @Operation(summary = "Change password for the authenticated user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.ok(passwordService.changePassword(authentication.getName(), request));
    }
}
