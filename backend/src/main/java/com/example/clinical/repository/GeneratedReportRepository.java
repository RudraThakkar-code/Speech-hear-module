package com.example.clinical.repository;

import com.example.clinical.domain.entity.GeneratedReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GeneratedReportRepository extends JpaRepository<GeneratedReport, UUID> {
    List<GeneratedReport> findByClinicalCase_Id(UUID caseId);
}
