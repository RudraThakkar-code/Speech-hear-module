package com.example.clinical.controller;

import com.example.clinical.domain.entity.*;
import com.example.clinical.service.TherapyWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TherapyWorkflowController {
    private final TherapyWorkflowService service;

    @PostMapping("/cases/{caseId}/therapy-goals")
    @ResponseStatus(HttpStatus.CREATED)
    public TherapyGoal createGoal(@PathVariable UUID caseId,
                                  @RequestParam(required = false) UUID encounterId,
                                  @RequestBody Map<String, Object> body) {
        return service.createGoal(caseId, encounterId, body);
    }

    @GetMapping("/cases/{caseId}/therapy-goals")
    public List<TherapyGoal> getGoals(@PathVariable UUID caseId) { return service.getGoals(caseId); }

    @PostMapping("/cases/{caseId}/therapy-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public TherapyPlan createPlan(@PathVariable UUID caseId, @RequestBody Map<String, Object> body) { return service.createPlan(caseId, body); }

    @PostMapping("/encounters/{encounterId}/therapy-session")
    @ResponseStatus(HttpStatus.CREATED)
    public TherapySession createSession(@PathVariable UUID encounterId, @RequestBody Map<String, Object> body) { return service.createSession(encounterId, body); }

    @PostMapping("/therapy-sessions/{sessionId}/home-practice")
    @ResponseStatus(HttpStatus.CREATED)
    public HomePractice createHomePractice(@PathVariable UUID sessionId, @RequestBody Map<String, Object> body) { return service.createHomePractice(sessionId, body); }

    @PostMapping("/encounters/{encounterId}/progress/{goalId}/{metricId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProgressRecord createProgress(@PathVariable UUID encounterId,
                                         @PathVariable UUID goalId,
                                         @PathVariable UUID metricId,
                                         @RequestBody Map<String, Object> body) {
        return service.createProgress(encounterId, goalId, metricId, body);
    }
}
