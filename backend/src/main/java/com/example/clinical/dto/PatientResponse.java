package com.example.clinical.dto;

import com.example.clinical.domain.enums.SexAtBirth;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PatientResponse {
    private UUID id;
    private String legalName;
    private String preferredName;
    private LocalDate dateOfBirth;
    private SexAtBirth sexAtBirth;
    private String genderIdentity;
    private String contactNumber;
    private String emailAddress;
    private String homeAddress;
}
