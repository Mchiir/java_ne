package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.billing.dto.request.CustomerRequest;
import rw.utility.billing.dto.response.CustomerResponse;
import rw.utility.billing.entity.Customer;
import rw.utility.billing.enums.Status;
import rw.utility.billing.exception.DuplicateResourceException;
import rw.utility.billing.exception.ResourceNotFoundException;
import rw.utility.billing.repository.CustomerRepository;
import rw.utility.billing.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        // Business rule: prevent duplicate customer registration (unique National ID).
        if (customerRepository.existsByNationalId(request.nationalId())) {
            throw new DuplicateResourceException(
                    "Customer already exists with National ID: " + request.nationalId());
        }
        Customer customer = Customer.builder()
                .fullNames(request.fullNames())
                .nationalId(request.nationalId())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .status(Status.ACTIVE)
                .build();
        // Link to an existing login account when the emails match, enabling the
        // customer self-service (/me) endpoints.
        if (request.email() != null) {
            userRepository.findByEmail(request.email()).ifPresent(customer::setUser);
        }
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream().map(CustomerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getCustomer(id));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getCustomer(id);
        // If changing National ID, keep it unique.
        if (!customer.getNationalId().equals(request.nationalId())
                && customerRepository.existsByNationalId(request.nationalId())) {
            throw new DuplicateResourceException(
                    "Customer already exists with National ID: " + request.nationalId());
        }
        customer.setFullNames(request.fullNames());
        customer.setNationalId(request.nationalId());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse updateStatus(Long id, Status status) {
        Customer customer = getCustomer(id);
        customer.setStatus(status);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    private Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }
}
