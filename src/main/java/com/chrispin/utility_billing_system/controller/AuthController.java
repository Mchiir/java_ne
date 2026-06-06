package com.chrispin.utility_billing_system.controller;

import com.chrispin.utility_billing_system.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.chrispin.utility_billing_system.dto.request.ChangePasswordRequest;
import com.chrispin.utility_billing_system.dto.request.ForgotPasswordRequest;
import com.chrispin.utility_billing_system.dto.request.LoginRequest;
import com.chrispin.utility_billing_system.dto.request.OtpRequest;
import com.chrispin.utility_billing_system.dto.request.OtpVerifyRequest;
import com.chrispin.utility_billing_system.dto.request.ResendCodeRequest;
import com.chrispin.utility_billing_system.dto.request.ResetPasswordRequest;
import com.chrispin.utility_billing_system.dto.request.UserRequest;
import com.chrispin.utility_billing_system.dto.response.JwtResponse;
import com.chrispin.utility_billing_system.dto.response.MessageResponse;
import com.chrispin.utility_billing_system.service.AuthService;
import com.chrispin.utility_billing_system.service.PasswordService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "User registration, login and password management")
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService;

    // ----- Sign-up & Verification -----

    @PostMapping("/signup")
    @Operation(summary = "Signup for Customers (emails a verification code)")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify the email with the signup code (returns a JWT)")
    public ResponseEntity<JwtResponse> verify(@Valid @RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyAccount(request));
    }

    @PostMapping("/verify/resend")
    @Operation(summary = "Resend the email-verification code")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.resendVerification(request));
    }

    @PostMapping("/code/resend")
    @Operation(summary = "Request a fresh code for any flow "
            + "(purpose = ACCOUNT_VERIFICATION | PASSWORD_RESET); invalidates the previous code")
    public ResponseEntity<MessageResponse> resendCode(@Valid @RequestBody ResendCodeRequest request) {
        return ResponseEntity.ok(authService.resendCode(request));
    }

    // ----- Login (email + password) -----

    @PostMapping("/login")
    @Operation(summary = "Authenticate with email + password and obtain a JWT (requires verified email)")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ----- Password (forgot / reset / change) -----

    @PostMapping("/password/forgot")
    @Operation(summary = "Request a password-reset code by email")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordService.forgotPassword(request));
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password using the emailed code")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordService.resetPassword(request));
    }

    @PostMapping("/password/change")
    @Operation(summary = "Change password for the authenticated user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.ok(passwordService.changePassword(authentication.getName(), request));
    }

    // ----- Get profile info -----
    @GetMapping("/me")
    @Operation(summary = "Get my profile info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(authService.getProfile());
    }
}
