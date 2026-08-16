package com.example.clinical.repository;
import com.example.clinical.domain.entity.ArticulationAssessmentContext;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ArticulationAssessmentContextRepository extends JpaRepository<ArticulationAssessmentContext, UUID> {}
