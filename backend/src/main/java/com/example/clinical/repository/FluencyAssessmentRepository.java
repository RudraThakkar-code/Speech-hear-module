package com.example.clinical.repository;
import com.example.clinical.domain.entity.FluencyAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.example.clinical.domain.entity.Encounter;
import java.util.List;
public interface FluencyAssessmentRepository extends JpaRepository<FluencyAssessment, UUID> {
    List<FluencyAssessment> findByEncounter(Encounter encounter);
}
