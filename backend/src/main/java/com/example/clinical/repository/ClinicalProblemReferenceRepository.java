package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalProblemReference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicalProblemReferenceRepository extends JpaRepository<ClinicalProblemReference, String> {
}
