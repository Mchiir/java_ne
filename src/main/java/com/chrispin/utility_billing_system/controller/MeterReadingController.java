package com.chrispin.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chrispin.utility_billing_system.dto.request.MeterReadingRequest;
import com.chrispin.utility_billing_system.dto.response.MeterReadingResponse;
import com.chrispin.utility_billing_system.service.MeterReadingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@Tag(name = "Meter Readings", description = "Capture and view meter readings (ROLE_OPERATOR captures)")
public class MeterReadingController {

    private final MeterReadingService readingService;

    @PostMapping
    @Operation(summary = "Capture a meter reading")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResponseEntity<MeterReadingResponse> capture(@Valid @RequestBody MeterReadingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readingService.capture(request));
    }

    @GetMapping
    @Operation(summary = "List all readings")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public List<MeterReadingResponse> findAll() {
        return readingService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reading by id")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public MeterReadingResponse findById(@PathVariable UUID id) {
        return readingService.findById(id);
    }

    @GetMapping("/meter/{meterId}")
    @Operation(summary = "List readings for a meter")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    public List<MeterReadingResponse> findByMeter(@PathVariable UUID meterId) {
        return readingService.findByMeter(meterId);
    }
}
