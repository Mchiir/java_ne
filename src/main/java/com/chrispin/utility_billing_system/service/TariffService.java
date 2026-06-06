package com.chrispin.utility_billing_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrispin.utility_billing_system.dto.request.TariffRequest;
import com.chrispin.utility_billing_system.dto.response.TariffResponse;
import com.chrispin.utility_billing_system.entity.Tariff;
import com.chrispin.utility_billing_system.enums.MeterType;
import com.chrispin.utility_billing_system.exception.ResourceNotFoundException;
import com.chrispin.utility_billing_system.repository.TariffRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;

    /**
     * Creates a new tariff version. Versions are per meter type and auto-increment.
     * The tariff applies only to billing cycles on/after {@code effectiveFrom}.
     */
    @Transactional
    public TariffResponse create(TariffRequest request) {
        int nextVersion = tariffRepository.findFirstByMeterTypeOrderByVersionDesc(request.meterType())
                .map(t -> t.getVersion() + 1)
                .orElse(1);
        Tariff tariff = Tariff.builder()
                .version(nextVersion)
                .meterType(request.meterType())
                .consumptionRate(request.consumptionRate())
                .fixedServiceCharge(request.fixedServiceCharge())
                .vatRate(request.vatRate())
                .penaltyRate(request.penaltyRate())
                .effectiveFrom(request.effectiveFrom())
                .active(true)
                .build();
        return TariffResponse.from(tariffRepository.save(tariff));
    }

    @Transactional(readOnly = true)
    public List<TariffResponse> findAll() {
        return tariffRepository.findAll().stream().map(TariffResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TariffResponse> findByType(MeterType type) {
        return tariffRepository.findByMeterTypeOrderByVersionDesc(type).stream()
                .map(TariffResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TariffResponse findById(UUID id) {
        return TariffResponse.from(getTariff(id));
    }

    /** Active tariff version effective for the given billing cycle. */
    @Transactional(readOnly = true)
    public Tariff resolveForCycle(MeterType type, LocalDate cycleStart) {
        return tariffRepository
                .findFirstByMeterTypeAndActiveTrueAndEffectiveFromLessThanEqualOrderByVersionDesc(type, cycleStart)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active tariff configured for " + type + " effective on/before " + cycleStart));
    }

    @Transactional
    public TariffResponse setActive(UUID id, boolean active) {
        Tariff tariff = getTariff(id);
        tariff.setActive(active);
        return TariffResponse.from(tariffRepository.save(tariff));
    }

    private Tariff getTariff(UUID id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff", "id", id));
    }
}
