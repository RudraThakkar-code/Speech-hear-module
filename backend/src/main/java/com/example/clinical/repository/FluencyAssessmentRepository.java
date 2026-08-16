package com.example.clinical.repository;
import com.example.clinical.domain.entity.FluencyAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface FluencyAssessmentRepository extends JpaRepository<FluencyAssessment, UUID> {}
