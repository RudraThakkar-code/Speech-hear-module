package com.example.clinical.repository;

import com.example.clinical.domain.entity.VoiceObjectiveMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VoiceObjectiveMeasurementRepository extends JpaRepository<VoiceObjectiveMeasurement, UUID> {
    List<VoiceObjectiveMeasurement> findByVoiceSample_Id(UUID sampleId);
}
