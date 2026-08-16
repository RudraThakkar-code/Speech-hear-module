package com.example.clinical.service;

import com.example.clinical.domain.entity.*;
import com.example.clinical.domain.enums.*;
import com.example.clinical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TherapyWorkflowService {
    private final TherapyGoalRepository goalRepository;
    private final GoalMetricRepository metricRepository;
    private final TherapyPlanRepository planRepository;
    private final TherapySessionRepository sessionRepository;
    private final HomePracticeRepository homePracticeRepository;
    private final ProgressRecordRepository progressRepository;
    private final ClinicalCaseRepository caseRepository;
    private final EncounterRepository encounterRepository;

    @Transactional
    public TherapyGoal createGoal(UUID caseId, UUID encounterId, Map<String, Object> body) {
        ClinicalCase clinicalCase = caseRepository.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found"));
        TherapyGoal goal = new TherapyGoal();
        goal.setClinicalCase(clinicalCase);
        if (encounterId != null) goal.setEncounter(encounterRepository.findById(encounterId).orElseThrow(() -> new IllegalArgumentException("Encounter not found")));
        goal.setGoalType(GoalType.valueOf(String.valueOf(body.get("goalType"))));
        goal.setDomain(TherapyDomain.valueOf(String.valueOf(body.get("domain"))));
        goal.setDescription(String.valueOf(body.get("description")));
        return goalRepository.save(goal);
    }

    @Transactional(readOnly = true)
    public List<TherapyGoal> getGoals(UUID caseId) { return goalRepository.findByClinicalCase_Id(caseId); }

    @Transactional
    public TherapyPlan createPlan(UUID caseId, Map<String, Object> body) {
        ClinicalCase clinicalCase = caseRepository.findById(caseId).orElseThrow(() -> new IllegalArgumentException("Case not found"));
        TherapyPlan plan = new TherapyPlan();
        plan.setClinicalCase(clinicalCase);
        plan.setPlanStartDate(LocalDate.parse(String.valueOf(body.get("planStartDate"))));
        Object end = body.get("planEndDate");
        if (end != null && !String.valueOf(end).isBlank()) plan.setPlanEndDate(LocalDate.parse(String.valueOf(end)));
        plan.setPlannedActivities(String.valueOf(body.get("plannedActivities")));
        plan.setFrequencyValue(((Number) body.get("frequencyValue")).intValue());
        plan.setFrequencyUnit(FrequencyUnit.valueOf(String.valueOf(body.get("frequencyUnit"))));
        plan.setSupervisorApprovalStatus(SupervisorApprovalStatus.valueOf(String.valueOf(body.get("supervisorApprovalStatus"))));
        Object goalIds = body.get("goalIds");
        if (goalIds instanceof Collection<?> ids) {
            for (Object id : ids) plan.getGoals().add(goalRepository.findById(UUID.fromString(String.valueOf(id))).orElseThrow(() -> new IllegalArgumentException("Goal not found")));
        }
        return planRepository.save(plan);
    }

    @Transactional
    public TherapySession createSession(UUID encounterId, Map<String, Object> body) {
        Encounter encounter = encounterRepository.findById(encounterId).orElseThrow(() -> new IllegalArgumentException("Encounter not found"));
        TherapySession session = new TherapySession();
        session.setEncounter(encounter);
        session.setSessionStatus(SessionStatus.valueOf(String.valueOf(body.get("sessionStatus"))));
        session.setActivitiesPerformed(String.valueOf(body.get("activitiesPerformed")));
        session.setPatientPerformance(String.valueOf(body.get("patientPerformance")));
        session.setHomeworkAssigned(body.get("homeworkAssigned") == null ? null : String.valueOf(body.get("homeworkAssigned")));
        return sessionRepository.save(session);
    }

    @Transactional
    public HomePractice createHomePractice(UUID sessionId, Map<String, Object> body) {
        TherapySession session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Therapy session not found"));
        HomePractice practice = new HomePractice();
        practice.setSession(session);
        practice.setAssignedAt(body.get("assignedAt") == null ? ZonedDateTime.now() : ZonedDateTime.parse(String.valueOf(body.get("assignedAt"))));
        practice.setDueAt(ZonedDateTime.parse(String.valueOf(body.get("dueAt"))));
        practice.setTaskDescription(String.valueOf(body.get("taskDescription")));
        practice.setCompletionStatus(CompletionStatus.valueOf(String.valueOf(body.get("completionStatus"))));
        practice.setPracticeMediaUrl(body.get("practiceMediaUrl") == null ? null : String.valueOf(body.get("practiceMediaUrl")));
        return homePracticeRepository.save(practice);
    }

    @Transactional
    public ProgressRecord createProgress(UUID encounterId, UUID goalId, UUID metricId, Map<String, Object> body) {
        ProgressRecord progress = new ProgressRecord();
        progress.setEncounter(encounterRepository.findById(encounterId).orElseThrow(() -> new IllegalArgumentException("Encounter not found")));
        progress.setGoal(goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("Goal not found")));
        progress.setMetric(metricRepository.findById(metricId).orElseThrow(() -> new IllegalArgumentException("Metric not found")));
        progress.setProgressStatus(ProgressStatus.valueOf(String.valueOf(body.get("progressStatus"))));
        progress.setTherapistProgressNote(String.valueOf(body.get("therapistProgressNote")));
        return progressRepository.save(progress);
    }
}
