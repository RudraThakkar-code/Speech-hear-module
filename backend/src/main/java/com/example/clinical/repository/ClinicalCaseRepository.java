package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClinicalCaseRepository extends JpaRepository<ClinicalCase, UUID> {
}
