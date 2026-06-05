package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.billing.dto.request.MeterReadingRequest;
import rw.utility.billing.dto.response.MeterReadingResponse;
import rw.utility.billing.entity.Meter;
import rw.utility.billing.entity.MeterReading;
import rw.utility.billing.enums.Status;
import rw.utility.billing.exception.BadRequestException;
import rw.utility.billing.exception.ResourceNotFoundException;
import rw.utility.billing.repository.MeterReadingRepository;
import rw.utility.billing.repository.MeterRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeterReadingService {

    private final MeterReadingRepository readingRepository;
    private final MeterRepository meterRepository;

    @Transactional
    public MeterReadingResponse capture(MeterReadingRequest request) {
        Meter meter = meterRepository.findById(request.meterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter", "id", request.meterId()));

        // Business rule: the meter must be active.
        if (meter.getStatus() != Status.ACTIVE) {
            throw new BadRequestException("Cannot capture a reading for an inactive meter: "
                    + meter.getMeterNumber());
        }

        int month = request.readingDate().getMonthValue();
        int year = request.readingDate().getYear();

        // Business rule: only one reading per meter per month/year.
        if (readingRepository.existsByMeter_IdAndReadingMonthAndReadingYear(meter.getId(), month, year)) {
            throw new BadRequestException(String.format(
                    "A reading already exists for meter %s in %02d/%d",
                    meter.getMeterNumber(), month, year));
        }

        // Resolve the previous reading: use the supplied value, else the last recorded current reading.
        BigDecimal previous = request.previousReading();
        if (previous == null) {
            previous = readingRepository.findTopByMeter_IdOrderByReadingYearDescReadingMonthDesc(meter.getId())
                    .map(MeterReading::getCurrentReading)
                    .orElse(BigDecimal.ZERO);
        }

        // Business rule: current reading must be greater than the previous reading.
        if (request.currentReading().compareTo(previous) <= 0) {
            throw new BadRequestException(String.format(
                    "Current reading (%s) must be greater than previous reading (%s)",
                    request.currentReading(), previous));
        }

        MeterReading reading = MeterReading.builder()
                .meter(meter)
                .previousReading(previous)
                .currentReading(request.currentReading())
                .consumption(request.currentReading().subtract(previous))
                .readingDate(request.readingDate())
                .readingMonth(month)
                .readingYear(year)
                .build();
        return MeterReadingResponse.from(readingRepository.save(reading));
    }

    @Transactional(readOnly = true)
    public List<MeterReadingResponse> findAll() {
        return readingRepository.findAll().stream().map(MeterReadingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MeterReadingResponse> findByMeter(Long meterId) {
        return readingRepository.findByMeter_Id(meterId).stream().map(MeterReadingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MeterReadingResponse findById(Long id) {
        return MeterReadingResponse.from(readingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MeterReading", "id", id)));
    }
}
