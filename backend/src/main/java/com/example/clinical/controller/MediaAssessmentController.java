package com.example.clinical.controller;

import com.example.clinical.domain.entity.AiArtifact;
import com.example.clinical.domain.entity.VideoObservation;
import com.example.clinical.domain.entity.VoiceAssessment;
import com.example.clinical.domain.entity.VoiceObjectiveMeasurement;
import com.example.clinical.service.MediaAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MediaAssessmentController {
    private final MediaAssessmentService service;

    @PostMapping("/encounters/{encounterId}/voice/{sampleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public VoiceAssessment createVoice(@PathVariable UUID encounterId, @PathVariable UUID sampleId, @RequestBody Map<String, Object> body) { return service.createVoice(encounterId, sampleId, body); }

    @PostMapping("/speech-samples/{sampleId}/voice-measurements")
    @ResponseStatus(HttpStatus.CREATED)
    public VoiceObjectiveMeasurement addMeasurement(@PathVariable UUID sampleId, @RequestBody Map<String, Object> body) { return service.addMeasurement(sampleId, body); }

    @PostMapping("/encounters/{encounterId}/video-observation")
    @ResponseStatus(HttpStatus.CREATED)
    public VideoObservation createVideo(@PathVariable UUID encounterId, @RequestBody Map<String, Object> body) { return service.createVideo(encounterId, body); }

    @PostMapping("/ai-artifacts")
    @ResponseStatus(HttpStatus.CREATED)
    public AiArtifact createAiArtifact(@RequestBody Map<String, Object> body) { return service.createAiArtifact(body); }
}
