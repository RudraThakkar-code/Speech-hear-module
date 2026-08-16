package com.example.clinical.controller;

import com.example.clinical.dto.FollowUpRequest;
import com.example.clinical.dto.FollowUpResponse;
import com.example.clinical.service.FollowUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters")
@RequiredArgsConstructor
public class FollowUpController {

    private final FollowUpService followUpService;

    @PostMapping("/{encounterId}/follow-up")
    public ResponseEntity<FollowUpResponse> createOrReplace(
            @PathVariable UUID encounterId,
            @Valid @RequestBody FollowUpRequest request) {
        return ResponseEntity.ok(followUpService.createOrReplace(encounterId, request));
    }
}
