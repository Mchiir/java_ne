package com.chrispin.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.chrispin.utility_billing_system.dto.request.PaymentRequest;
import com.chrispin.utility_billing_system.dto.response.PaymentResponse;
import com.chrispin.utility_billing_system.service.PaymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Record payments and view history")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Record a payment against a bill (ROLE_FINANCE)")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    public ResponseEntity<PaymentResponse> record(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.record(request));
    }

    @GetMapping
    @Operation(summary = "List all payments")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    public List<PaymentResponse> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/bill/{billId}")
    @Operation(summary = "List payments for a bill")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    public List<PaymentResponse> findByBill(@PathVariable UUID billId) {
        return paymentService.findByBill(billId);
    }

    @GetMapping("/me")
    @Operation(summary = "View my own payment history (ROLE_CUSTOMER)")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<PaymentResponse> findMine(Authentication authentication) {
        return paymentService.findMine(authentication.getName());
    }
}
