package com.example.clinical.controller;

import com.example.clinical.dto.EvidenceSummaryResponse;
import com.example.clinical.service.EvidenceSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters")
@RequiredArgsConstructor
public class EvidenceSummaryController {

    private final EvidenceSummaryService evidenceSummaryService;

    @GetMapping("/{encounterId}/evidence-summary")
    public ResponseEntity<EvidenceSummaryResponse> getEvidenceSummary(@PathVariable UUID encounterId) {
        return ResponseEntity.ok(evidenceSummaryService.getEvidenceSummary(encounterId));
    }
}
