package com.example.clinical.controller;

import com.example.clinical.domain.entity.*;
import com.example.clinical.service.DoctorPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorPortalController {
    private final DoctorPortalService service;

    @PostMapping("/cases/{caseId}/assign/{doctorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorCaseAssignment assign(@PathVariable UUID caseId, @PathVariable UUID doctorId, @RequestBody Map<String, Object> body) { return service.assign(caseId, doctorId, body); }

    @GetMapping("/cases/{caseId}/assignments")
    public List<DoctorCaseAssignment> assignments(@PathVariable UUID caseId) { return service.assignments(caseId); }

    @PostMapping("/cases/{caseId}/discussions")
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicalDiscussion discussion(@PathVariable UUID caseId, @RequestParam UUID doctorId, @RequestBody Map<String, Object> body) { return service.createDiscussion(caseId, doctorId, body); }

    @PostMapping("/discussions/{discussionId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicalDiscussionMessage message(@PathVariable UUID discussionId, @RequestParam UUID authorId, @RequestBody Map<String, Object> body) { return service.addMessage(discussionId, authorId, body); }

    @GetMapping("/discussions/{discussionId}/messages")
    public List<ClinicalDiscussionMessage> messages(@PathVariable UUID discussionId) { return service.messages(discussionId); }

    @PostMapping("/cases/{caseId}/recommendations")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorRecommendation recommendation(@PathVariable UUID caseId, @RequestParam UUID doctorId, @RequestBody Map<String, Object> body) { return service.recommend(caseId, doctorId, body); }

    @GetMapping("/cases/{caseId}/recommendations")
    public List<DoctorRecommendation> recommendations(@PathVariable UUID caseId) { return service.recommendations(caseId); }
}
