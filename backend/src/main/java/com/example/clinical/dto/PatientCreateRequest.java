package com.example.clinical.dto;

import com.example.clinical.domain.enums.SexAtBirth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientCreateRequest {

    @NotBlank(message = "Legal name cannot be blank")
    private String legalName;

    private String preferredName;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    private SexAtBirth sexAtBirth;

    private String genderIdentity;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    private String emailAddress;

    private String homeAddress;
}
