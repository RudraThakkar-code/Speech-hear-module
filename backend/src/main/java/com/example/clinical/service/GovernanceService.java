package com.example.clinical.service;

import com.example.clinical.domain.entity.AuditLog;
import com.example.clinical.domain.entity.Consent;
import com.example.clinical.domain.entity.Patient;
import com.example.clinical.repository.AuditLogRepository;
import com.example.clinical.repository.ConsentRepository;
import com.example.clinical.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GovernanceService {
    private final ConsentRepository consentRepository;
    private final PatientRepository patientRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public Consent recordConsent(UUID patientId, Map<String, Object> body) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Consent c = new Consent();
        c.setPatient(patient);
        c.setConsentVersion(((Number) body.getOrDefault("consentVersion", 1)).intValue());
        c.setConsentedAt(ZonedDateTime.now());
        c.setDataCollection(Boolean.parseBoolean(String.valueOf(body.getOrDefault("consentDataCollection", false))));
        c.setAudioVideo(Boolean.parseBoolean(String.valueOf(body.getOrDefault("consentAudioVideo", false))));
        c.setAiAnalysis(Boolean.parseBoolean(String.valueOf(body.getOrDefault("consentAiAnalysis", false))));
        c.setAiTraining(String.valueOf(body.getOrDefault("consentAiTraining", "NOT_ASKED")));
        c.setSignatureUrl(String.valueOf(body.get("consentSignatureUrl")));
        return consentRepository.save(c);
    }

    @Transactional(readOnly = true)
    public List<Consent> consents(UUID patientId) { return consentRepository.findByPatient_IdOrderByConsentVersionDesc(patientId); }

    @Transactional(readOnly = true)
    public List<AuditLog> audit(String tableName, UUID recordId) { return auditLogRepository.findByTableNameAndRecordIdOrderByOccurredAtDesc(tableName, recordId); }
}
