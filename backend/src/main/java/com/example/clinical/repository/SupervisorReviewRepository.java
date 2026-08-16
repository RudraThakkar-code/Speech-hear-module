package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalInterpretation;
import com.example.clinical.domain.entity.SupervisorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupervisorReviewRepository extends JpaRepository<SupervisorReview, UUID> {
    Optional<SupervisorReview> findByReviewedClinicalInterpretation(ClinicalInterpretation interpretation);
}
