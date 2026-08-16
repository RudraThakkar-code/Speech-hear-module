package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClinicalDiscussionRepository extends JpaRepository<ClinicalDiscussion, UUID> {
    List<ClinicalDiscussion> findByClinicalCase_IdOrderByCreatedAtDesc(UUID caseId);
}
