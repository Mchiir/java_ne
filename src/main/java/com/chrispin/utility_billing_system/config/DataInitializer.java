package com.chrispin.utility_billing_system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.chrispin.utility_billing_system.entity.Role;
import com.chrispin.utility_billing_system.entity.User;
import com.chrispin.utility_billing_system.enums.ERole;
import com.chrispin.utility_billing_system.enums.Status;
import com.chrispin.utility_billing_system.repository.RoleRepository;
import com.chrispin.utility_billing_system.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Seeds the four roles and a default ROLE_ADMIN account on first boot.
 * Runs before the database-routine initializer.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.full-names}")
    private String adminFullNames;
    @Value("${app.admin.phone}")
    private String adminPhone;

    @Override
    public void run(String... args) {
        Arrays.stream(ERole.values()).forEach(this::ensureRole);

        // Default staff accounts (pre-verified). Customers self-register via signup.
        ensureUser(adminEmail, adminFullNames, adminPhone, adminPassword, ERole.ROLE_ADMIN);
        ensureUser("operator@utilitybilling.com", "Default Operator", "+250781234567",
                "Operator@123", ERole.ROLE_OPERATOR);
        ensureUser("finance@utilitybilling.com", "Default Finance", "+250781234568",
                "Finance@123", ERole.ROLE_FINANCE);
    }

    private void ensureUser(String email, String fullNames, String phone, String rawPassword, ERole role) {
        userRepository.findByEmail(email).ifPresentOrElse(existing -> {
            // Keep seeded staff usable after the email-verification gate was added.
            if (!existing.isEmailVerified()) {
                existing.setEmailVerified(true);
                userRepository.save(existing);
                log.info("Marked existing {} as verified", email);
            }
        }, () -> {
            Role r = roleRepository.findByName(role).orElseThrow();
            Set<Role> roles = new HashSet<>();
            roles.add(r);
            User user = User.builder()
                    .fullNames(fullNames)
                    .email(email)
                    .phoneNumber(phone)
                    .password(passwordEncoder.encode(rawPassword))
                    .status(Status.ACTIVE)
                    .emailVerified(true)
                    .roles(roles)
                    .build();
            userRepository.save(user);
            log.info("Seeded default {} account: {}", role, email);
        });
    }

    private void ensureRole(ERole name) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(new Role(name));
            log.info("Seeded role: {}", name);
        }
    }
}
