package com.chrispin.utility_billing_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrispin.utility_billing_system.dto.response.UserResponse;
import com.chrispin.utility_billing_system.entity.User;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.exception.ResourceNotFoundException;
import com.chrispin.utility_billing_system.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return UserResponse.from(getUser(id));
    }

    @Transactional
    public UserResponse updateStatus(UUID id, Status status) {
        User user = getUser(id);
        user.setStatus(status);
        return UserResponse.from(userRepository.save(user));
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
