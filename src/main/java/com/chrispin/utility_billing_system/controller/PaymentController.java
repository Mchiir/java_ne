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
import rw.utility.billing.dto.request.PaymentRequest;
import rw.utility.billing.dto.response.PaymentResponse;
import rw.utility.billing.service.PaymentService;

import java.util.List;

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
    public List<PaymentResponse> findByBill(@PathVariable Long billId) {
        return paymentService.findByBill(billId);
    }

    @GetMapping("/me")
    @Operation(summary = "View my own payment history (ROLE_CUSTOMER)")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<PaymentResponse> findMine(Authentication authentication) {
        return paymentService.findMine(authentication.getName());
    }
}
