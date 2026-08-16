package com.example.clinical.repository;

import com.example.clinical.domain.entity.VoiceAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VoiceAssessmentRepository extends JpaRepository<VoiceAssessment, UUID> {
    List<VoiceAssessment> findByEncounter_Id(UUID encounterId);
}
