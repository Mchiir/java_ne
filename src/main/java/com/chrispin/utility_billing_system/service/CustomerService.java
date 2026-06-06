package com.chrispin.utility_billing_system.service;

import com.chrispin.utility_billing_system.dto.request.UpdateUserRequest;
import com.chrispin.utility_billing_system.dto.request.UserRequest;
import com.chrispin.utility_billing_system.dto.response.UserResponse;
import com.chrispin.utility_billing_system.entity.Role;
import com.chrispin.utility_billing_system.entity.User;
import com.chrispin.utility_billing_system.enums.ERole;
import com.chrispin.utility_billing_system.exception.BadRequestException;
import com.chrispin.utility_billing_system.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.exception.DuplicateResourceException;
import com.chrispin.utility_billing_system.exception.ResourceNotFoundException;
import com.chrispin.utility_billing_system.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(UserRequest request) {
        // Business rule: prevent duplicate customer registration (unique National ID).
        if (userRepository.existsByNationalId(request.nationalId())) {
            throw new DuplicateResourceException(
                    "Customer already exists with National ID: " + request.nationalId());
        }

        // provide role (customer)
        Set<Role> roles = new HashSet<>();
        roles.add(getRole(ERole.ROLE_CUSTOMER));



        User customer = User.builder()
                .fullNames(request.fullNames())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .address(request.address())
                .nationalId(request.nationalId())
                .phoneNumber(request.phoneNumber())
                .status(Status.ACTIVE)
                .emailVerified(false)
                .roles(roles)
                .build();

        return UserResponse.from(userRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findByRole(ERole.ROLE_CUSTOMER)
                .stream().
                map(UserResponse::from).
                toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return UserResponse.from(getCustomer(id));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User customer = getCustomer(id);
        // If changing National ID, keep it unique.
        if (!customer.getNationalId().equals(request.nationalId())
                && userRepository.existsByNationalId(request.nationalId())) {
            throw new DuplicateResourceException(
                    "Customer already exists with National ID: " + request.nationalId());
        }
        customer.setFullNames(request.fullNames());
        customer.setNationalId(request.nationalId());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());
        return UserResponse.from(userRepository.save(customer));
    }

    @Transactional
    public UserResponse updateStatus(UUID id, Status status) {
        User customer = getCustomer(id);
        customer.setStatus(status);
        return UserResponse.from(userRepository.save(customer));
    }

    private User getCustomer(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }

    private Role getRole(ERole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new BadRequestException("Role not configured: " + name));
    }
}
