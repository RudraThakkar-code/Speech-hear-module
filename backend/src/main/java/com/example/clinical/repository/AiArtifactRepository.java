package com.example.clinical.repository;

import com.example.clinical.domain.entity.AiArtifact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiArtifactRepository extends JpaRepository<AiArtifact, UUID> {
    List<AiArtifact> findByClinicalInterpretation_ClinicalInterpretationId(UUID interpretationId);
}
