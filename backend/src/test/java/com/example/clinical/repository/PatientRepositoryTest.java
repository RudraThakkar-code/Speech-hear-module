package com.example.clinical.repository;

import com.example.clinical.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Test
    void contextLoads() {
        // Validation handled purely via Spring Context loading all repositories successfully
    }
}
