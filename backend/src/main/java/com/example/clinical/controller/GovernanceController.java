package com.example.clinical.controller;

import com.example.clinical.domain.entity.AuditLog;
import com.example.clinical.domain.entity.Consent;
import com.example.clinical.service.GovernanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GovernanceController {
    private final GovernanceService service;

    @PostMapping("/patients/{patientId}/consent")
    @ResponseStatus(HttpStatus.CREATED)
    public Consent recordConsent(@PathVariable UUID patientId, @RequestBody Map<String, Object> body) { return service.recordConsent(patientId, body); }

    @GetMapping("/patients/{patientId}/consent")
    public List<Consent> consents(@PathVariable UUID patientId) { return service.consents(patientId); }

    @GetMapping("/audit/{tableName}/{recordId}")
    public List<AuditLog> audit(@PathVariable String tableName, @PathVariable UUID recordId) { return service.audit(tableName, recordId); }
}
