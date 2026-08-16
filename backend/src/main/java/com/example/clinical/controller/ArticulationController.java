package com.example.clinical.controller;

import com.example.clinical.dto.ArticulationAttemptCreateRequest;
import com.example.clinical.dto.ArticulationAttemptResponse;
import com.example.clinical.dto.ArticulationContextCreateRequest;
import com.example.clinical.dto.ArticulationContextResponse;
import com.example.clinical.service.ArticulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters/{encounterId}/articulation")
@RequiredArgsConstructor
public class ArticulationController {

    private final ArticulationService articulationService;

    @PostMapping("/contexts")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticulationContextResponse createContext(
            @PathVariable UUID encounterId,
            @RequestBody ArticulationContextCreateRequest request) {
        return articulationService.createContext(encounterId, request);
    }

    @PostMapping("/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticulationAttemptResponse createAttempt(
            @PathVariable UUID encounterId,
            @RequestBody ArticulationAttemptCreateRequest request) {
        return articulationService.createAttempt(encounterId, request);
    }

    @GetMapping("/attempts")
    public List<ArticulationAttemptResponse> getAttempts(@PathVariable UUID encounterId) {
        return articulationService.getAttemptsByEncounter(encounterId);
    }
}
