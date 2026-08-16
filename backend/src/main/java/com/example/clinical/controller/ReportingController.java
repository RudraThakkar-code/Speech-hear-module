package com.example.clinical.controller;

import com.example.clinical.domain.entity.GeneratedReport;
import com.example.clinical.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases/{caseId}/reports")
@RequiredArgsConstructor
public class ReportingController {
    private final ReportingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeneratedReport generate(@PathVariable UUID caseId, @RequestBody Map<String, Object> body) { return service.generate(caseId, body); }

    @GetMapping
    public List<GeneratedReport> list(@PathVariable UUID caseId) { return service.list(caseId); }
}
