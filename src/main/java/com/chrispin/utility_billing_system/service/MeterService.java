package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.billing.dto.request.MeterRequest;
import rw.utility.billing.dto.response.MeterResponse;
import rw.utility.billing.entity.Customer;
import rw.utility.billing.entity.Meter;
import rw.utility.billing.enums.Status;
import rw.utility.billing.exception.DuplicateResourceException;
import rw.utility.billing.exception.ResourceNotFoundException;
import rw.utility.billing.repository.CustomerRepository;
import rw.utility.billing.repository.MeterRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeterService {

    private final MeterRepository meterRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public MeterResponse create(MeterRequest request) {
        if (meterRepository.existsByMeterNumber(request.meterNumber())) {
            throw new DuplicateResourceException("Meter already exists: " + request.meterNumber());
        }
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.customerId()));
        Meter meter = Meter.builder()
                .meterNumber(request.meterNumber())
                .meterType(request.meterType())
                .installationDate(request.installationDate())
                .status(Status.ACTIVE)
                .customer(customer)
                .build();
        return MeterResponse.from(meterRepository.save(meter));
    }

    @Transactional(readOnly = true)
    public List<MeterResponse> findAll() {
        return meterRepository.findAll().stream().map(MeterResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MeterResponse> findByCustomer(Long customerId) {
        return meterRepository.findByCustomer_Id(customerId).stream().map(MeterResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MeterResponse findById(Long id) {
        return MeterResponse.from(getMeter(id));
    }

    @Transactional
    public MeterResponse updateStatus(Long id, Status status) {
        Meter meter = getMeter(id);
        meter.setStatus(status);
        return MeterResponse.from(meterRepository.save(meter));
    }

    private Meter getMeter(Long id) {
        return meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter", "id", id));
    }
}
