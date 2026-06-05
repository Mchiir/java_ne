package com.chrispin.utility_billing_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chrispin.utility_billing_system.dto.request.TariffRequest;
import com.chrispin.utility_billing_system.dto.response.TariffResponse;
import com.chrispin.utility_billing_system.enums.MeterType;
import com.chrispin.utility_billing_system.service.TariffService;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
@Tag(name = "Tariffs", description = "Tariff, tax and penalty configuration (ROLE_ADMIN)")
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    @Operation(summary = "Configure a new tariff version")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TariffResponse> create(@Valid @RequestBody TariffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tariffService.create(request));
    }

    @GetMapping
    @Operation(summary = "List all tariffs")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    public List<TariffResponse> findAll() {
        return tariffService.findAll();
    }

    @GetMapping("/type/{meterType}")
    @Operation(summary = "List tariff versions for a meter type")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    public List<TariffResponse> findByType(@PathVariable MeterType meterType) {
        return tariffService.findByType(meterType);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a tariff by id")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    public TariffResponse findById(@PathVariable Long id) {
        return tariffService.findById(id);
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Enable / disable a tariff version")
    @PreAuthorize("hasRole('ADMIN')")
    public TariffResponse setActive(@PathVariable Long id, @RequestParam boolean active) {
        return tariffService.setActive(id, active);
    }
}
