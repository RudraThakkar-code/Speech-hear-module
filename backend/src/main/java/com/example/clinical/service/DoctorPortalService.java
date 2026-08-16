package com.example.clinical.service;

import com.example.clinical.domain.entity.*;
import com.example.clinical.domain.enums.ClinicalDiscussionPriority;
import com.example.clinical.domain.enums.ClinicalDiscussionStatus;
import com.example.clinical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorPortalService {
    private final DoctorCaseAssignmentRepository assignmentRepository;
    private final ClinicalDiscussionRepository discussionRepository;
    private final ClinicalDiscussionMessageRepository messageRepository;
    private final DoctorRecommendationRepository recommendationRepository;
    private final ClinicalCaseRepository caseRepository;
    private final EncounterRepository encounterRepository;
    private final UserRepository userRepository;

    @Transactional
    public DoctorCaseAssignment assign(UUID caseId, UUID doctorId, Map<String, Object> body) {
        DoctorCaseAssignment a = new DoctorCaseAssignment();
        a.setClinicalCase(caseRepository.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found")));
        a.setDoctor(userRepository.findById(doctorId).orElseThrow(() -> new IllegalArgumentException("Doctor not found")));
        a.setRole(String.valueOf(body.getOrDefault("role", "CONSULTING_DOCTOR")));
        a.setStatus(String.valueOf(body.getOrDefault("status", "ACTIVE")));
        a.setAssignedAt(ZonedDateTime.now());
        return assignmentRepository.save(a);
    }

    @Transactional(readOnly = true)
    public List<DoctorCaseAssignment> assignments(UUID caseId) { return assignmentRepository.findByClinicalCase_Id(caseId); }

    @Transactional
    public ClinicalDiscussion createDiscussion(UUID caseId, UUID doctorId, Map<String, Object> body) {
        ClinicalDiscussion d = new ClinicalDiscussion();
        d.setClinicalCase(caseRepository.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found")));
        if (body.get("encounterId") != null) d.setEncounter(encounterRepository.findById(UUID.fromString(String.valueOf(body.get("encounterId")))).orElseThrow(() -> new IllegalArgumentException("Encounter not found")));
        d.setTopic(String.valueOf(body.get("topic")));
        d.setStatus(ClinicalDiscussionStatus.valueOf(String.valueOf(body.getOrDefault("status", "OPEN"))));
        d.setPriority(ClinicalDiscussionPriority.valueOf(String.valueOf(body.getOrDefault("priority", "ROUTINE"))));
        d.setCreatedByUser(userRepository.findById(doctorId).orElseThrow(() -> new IllegalArgumentException("Doctor not found")));
        d.setCreatedAt(ZonedDateTime.now());
        return discussionRepository.save(d);
    }

    @Transactional
    public ClinicalDiscussionMessage addMessage(UUID discussionId, UUID authorId, Map<String, Object> body) {
        ClinicalDiscussionMessage m = new ClinicalDiscussionMessage();
        m.setDiscussion(discussionRepository.findById(discussionId).orElseThrow(() -> new IllegalArgumentException("Discussion not found")));
        m.setAuthor(userRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found")));
        m.setMessageText(String.valueOf(body.get("messageText")));
        m.setCreatedAt(ZonedDateTime.now());
        if (body.get("linkedDocumentId") != null) m.setLinkedDocumentId(UUID.fromString(String.valueOf(body.get("linkedDocumentId"))));
        if (body.get("linkedRecordId") != null) m.setLinkedRecordId(UUID.fromString(String.valueOf(body.get("linkedRecordId"))));
        return messageRepository.save(m);
    }

    @Transactional(readOnly = true)
    public List<ClinicalDiscussionMessage> messages(UUID discussionId) { return messageRepository.findByDiscussion_IdOrderByCreatedAtAsc(discussionId); }

    @Transactional
    public DoctorRecommendation recommend(UUID caseId, UUID doctorId, Map<String, Object> body) {
        DoctorRecommendation r = new DoctorRecommendation();
        r.setClinicalCase(caseRepository.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found")));
        r.setDoctor(userRepository.findById(doctorId).orElseThrow(() -> new IllegalArgumentException("Doctor not found")));
        if (body.get("discussionId") != null) r.setDiscussion(discussionRepository.findById(UUID.fromString(String.valueOf(body.get("discussionId")))).orElseThrow(() -> new IllegalArgumentException("Discussion not found")));
        r.setRecommendationText(String.valueOf(body.get("recommendationText")));
        r.setMedicalDiagnosis(body.get("medicalDiagnosis") == null ? null : String.valueOf(body.get("medicalDiagnosis")));
        r.setCreatedAt(ZonedDateTime.now());
        return recommendationRepository.save(r);
    }

    @Transactional(readOnly = true)
    public List<DoctorRecommendation> recommendations(UUID caseId) { return recommendationRepository.findByClinicalCase_IdOrderByCreatedAtDesc(caseId); }
}
