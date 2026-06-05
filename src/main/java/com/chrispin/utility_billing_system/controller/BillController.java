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
import rw.utility.billing.dto.request.BillGenerateRequest;
import rw.utility.billing.dto.response.BillResponse;
import rw.utility.billing.service.BillService;

import java.util.List;  

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Tag(name = "Bills", description = "Bill generation, approval and viewing")
public class BillController {

    private final BillService billService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a postpaid bill from a meter reading")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public ResponseEntity<BillResponse> generate(@Valid @RequestBody BillGenerateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billService.generate(request));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve a bill (ROLE_ADMIN / ROLE_FINANCE)")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    public BillResponse approve(@PathVariable Long id, Authentication authentication) {
        return billService.approve(id, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "List all bills")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    public List<BillResponse> findAll() {
        return billService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a bill by id")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','OPERATOR')")
    public BillResponse findById(@PathVariable Long id) {
        return billService.findById(id);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List bills for a customer")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','OPERATOR')")
    public List<BillResponse> findByCustomer(@PathVariable Long customerId) {
        return billService.findByCustomer(customerId);
    }

    @GetMapping("/me")
    @Operation(summary = "View my own bills (ROLE_CUSTOMER)")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<BillResponse> findMine(Authentication authentication) {
        return billService.findMine(authentication.getName());
    }
}
