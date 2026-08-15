package com.example.clinical.controller;

import com.example.clinical.dto.PatientCreateRequest;
import com.example.clinical.dto.PatientResponse;
import com.example.clinical.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse createPatient(@Valid @RequestBody PatientCreateRequest request) {
        return patientService.createPatient(request);
    }

    @GetMapping("/{patientId}")
    public PatientResponse getPatient(@PathVariable UUID patientId) {
        return patientService.getPatient(patientId);
    }
}
