package com.example.clinical;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates the real PostgreSQL migration chain and Hibernate schema contract.
 *
 * This test is intentionally opt-in so ordinary H2 tests remain self-contained.
 * Set RUN_POSTGRES_VALIDATION=true and provide PostgreSQL connection variables
 * before running this test.
 */
@SpringBootTest
@ActiveProfiles("postgres-validation")
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_VALIDATION", matches = "true")
class PostgreSQLMigrationValidationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplyAndValidateFlywayMigrations() {
        assertTrue(flyway.validateWithResult().validationSuccessful,
                "Flyway migration validation should succeed");

        assertTableExists("clinical_interpretation");
        assertTableExists("clinical_interpretation_history");
        assertTableExists("clinical_interpretation_problem_history");
        assertTableExists("correction_request");
        assertTableExists("supervisor_review");
        assertTableExists("supervisor_review_history");
        assertTableExists("clinical_problem_reference");
        assertTableExists("language_reference");
    }

    private void assertTableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?",
                Integer.class,
                tableName);
        assertTrue(count != null && count == 1, "Expected PostgreSQL table to exist: " + tableName);
    }
}
