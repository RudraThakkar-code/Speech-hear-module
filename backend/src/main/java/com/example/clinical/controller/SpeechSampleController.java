package com.example.clinical.controller;

import com.example.clinical.dto.SpeechSampleCreateRequest;
import com.example.clinical.dto.SpeechSampleResponse;
import com.example.clinical.service.SpeechSampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assignments/{assignmentId}/samples")
@RequiredArgsConstructor
public class SpeechSampleController {

    private final SpeechSampleService speechSampleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpeechSampleResponse createSpeechSample(
            @PathVariable UUID assignmentId,
            @RequestBody SpeechSampleCreateRequest request) {
        return speechSampleService.createSpeechSample(assignmentId, request);
    }

    @GetMapping
    public List<SpeechSampleResponse> getSpeechSamples(@PathVariable UUID assignmentId) {
        return speechSampleService.getSpeechSamplesByAssignment(assignmentId);
    }
}
