package com.chrispin.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chrispin.utility_billing_system.dto.request.MeterRequest;
import com.chrispin.utility_billing_system.dto.response.MeterResponse;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.service.MeterService;

import java.util.List;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
@Tag(name = "Meters", description = "Meter management")
public class MeterController {

    private final MeterService meterService;

    @PostMapping
    @Operation(summary = "Register a new meter for a customer")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public ResponseEntity<MeterResponse> create(@Valid @RequestBody MeterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meterService.create(request));
    }

    @GetMapping
    @Operation(summary = "List all meters")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public List<MeterResponse> findAll() {
        return meterService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a meter by id")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public MeterResponse findById(@PathVariable Long id) {
        return meterService.findById(id);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List meters belonging to a customer")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public List<MeterResponse> findByCustomer(@PathVariable Long customerId) {
        return meterService.findByCustomer(customerId);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate / deactivate a meter")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public MeterResponse updateStatus(@PathVariable Long id, @RequestParam Status status) {
        return meterService.updateStatus(id, status);
    }
}
