package com.example.clinical.controller;

import com.example.clinical.dto.EncounterResponse;
import com.example.clinical.service.EncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters")
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterService encounterService;

    @GetMapping("/{encounterId}")
    public EncounterResponse getEncounter(@PathVariable UUID encounterId) {
        return encounterService.getEncounter(encounterId);
    }
}
