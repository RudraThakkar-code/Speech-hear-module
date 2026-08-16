package com.example.clinical.service;

import com.example.clinical.domain.entity.ClinicalCase;
import com.example.clinical.domain.entity.GeneratedReport;
import com.example.clinical.domain.enums.ReportType;
import com.example.clinical.repository.ClinicalCaseRepository;
import com.example.clinical.repository.GeneratedReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportingService {
    private final GeneratedReportRepository reportRepository;
    private final ClinicalCaseRepository caseRepository;

    @Transactional
    public GeneratedReport generate(UUID caseId, Map<String, Object> body) {
        ClinicalCase clinicalCase = caseRepository.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found"));
        GeneratedReport report = new GeneratedReport();
        report.setClinicalCase(clinicalCase);
        report.setReportType(ReportType.valueOf(String.valueOf(body.get("reportType"))));
        report.setReportContent(body.getOrDefault("reportContent", Map.of("status", "generated")));
        report.setGeneratedAt(ZonedDateTime.now());
        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<GeneratedReport> list(UUID caseId) { return reportRepository.findByClinicalCase_Id(caseId); }
}
