package com.chrispin.utility_billing_system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Applies the PL/pgSQL trigger functions and triggers in db/routines.sql once the
 * JPA-managed tables exist. Runs after {@link DataInitializer}. The script is
 * idempotent, so it is safe to run on every startup.
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class DatabaseRoutineInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        String sql = StreamUtils.copyToString(
                new ClassPathResource("db/routines.sql").getInputStream(), StandardCharsets.UTF_8);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // The PostgreSQL JDBC driver executes multiple ';'-separated statements,
            // including dollar-quoted function bodies, in a single call.
            statement.execute(sql);
            log.info("Database routines (triggers/functions) applied successfully.");
        } catch (Exception e) {
            log.error("Failed to apply database routines: {}", e.getMessage(), e);
            throw e;
        }
    }
}
