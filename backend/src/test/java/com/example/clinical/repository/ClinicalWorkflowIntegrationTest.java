package com.example.clinical.repository;

import com.example.clinical.DemoApplication;
import com.example.clinical.domain.entity.*;
import com.example.clinical.domain.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
@Transactional
class ClinicalWorkflowIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ClinicalCaseRepository clinicalCaseRepository;
    @Autowired private EncounterRepository encounterRepository;
    @Autowired private LanguageReferenceRepository languageReferenceRepository;
    @Autowired private LanguageHistoryRepository languageHistoryRepository;
    @Autowired private BirthHistoryRepository birthHistoryRepository;

    @Test
    void shouldExecuteCoreClinicalWorkflow() {
        // 1. Create Therapist & Supervisor
        User therapist = new User();
        therapist.setName("Dr. Therapist");
        therapist.setRole(UserRole.THERAPIST);
        therapist.setStatus(UserStatus.ACTIVE);
        therapist = userRepository.saveAndFlush(therapist);

        User supervisor = new User();
        supervisor.setName("Dr. Supervisor");
        supervisor.setRole(UserRole.SUPERVISOR);
        supervisor.setStatus(UserStatus.ACTIVE);
        supervisor = userRepository.saveAndFlush(supervisor);

        // 2. Create Patient
        Patient patient = new Patient();
        patient.setLegalName("John Doe");
        patient.setDateOfBirth(LocalDate.of(2018, 8, 15));
        patient.setContactNumber("555-1234");
        patient = patientRepository.saveAndFlush(patient);

        // 3. Create Case
        ClinicalCase clinicalCase = new ClinicalCase();
        clinicalCase.setPatient(patient);
        clinicalCase.setCaseStatus(CaseStatus.ACTIVE);
        clinicalCase.setAssignedTherapist(therapist);
        clinicalCase.setAssignedSupervisor(supervisor);
        clinicalCase = clinicalCaseRepository.saveAndFlush(clinicalCase);

        // 4. Create Language Reference (Seed Data Simulation)
        LanguageReference english = new LanguageReference();
        english.setLanguageCode("EN");
        english.setLanguageName("English");
        english = languageReferenceRepository.saveAndFlush(english);

        // 5. Create History Domains
        LanguageHistory langHistory = new LanguageHistory();
        langHistory.setClinicalCase(clinicalCase);
        langHistory.setLanguageSpokenByChild(english);
        langHistory.getLanguagesUnderstood().add(english);
        langHistory.getLanguagesHeardAtHome().add(english);
        langHistory = languageHistoryRepository.saveAndFlush(langHistory);

        BirthHistory birthHistory = new BirthHistory();
        birthHistory.setClinicalCase(clinicalCase);
        birthHistory.setPregnancyComplications(false);
        birthHistory.setDeliveryType(DeliveryType.VAGINAL);
        birthHistory.setPrematurityStatus(PrematurityStatus.FULL_TERM);
        birthHistory.setNicuAdmission(false);
        birthHistory.setBirthWeight(new BigDecimal("3.5"));
        birthHistory = birthHistoryRepository.saveAndFlush(birthHistory);

        // 6. Create Encounter anchoring the histories
        Encounter encounter = new Encounter();
        encounter.setClinicalCase(clinicalCase);
        encounter.setEncounterType(EncounterType.INITIAL_EVALUATION);
        encounter.setEncounterDateTime(ZonedDateTime.now());
        encounter.setAssessmentReason(AssessmentReason.CHIEF_COMPLAINT);
        encounter.setActiveLanguageHistory(langHistory);
        encounter.setActiveBirthHistory(birthHistory);
        encounter = encounterRepository.saveAndFlush(encounter);

        // Assertions verifying the graph is linked properly
        assertThat(encounter.getId()).isNotNull();

        Optional<Encounter> retrievedEncounter = encounterRepository.findById(encounter.getId());
        assertThat(retrievedEncounter).isPresent();
        assertThat(retrievedEncounter.get().getClinicalCase().getAssignedTherapist().getName()).isEqualTo("Dr. Therapist");
        assertThat(retrievedEncounter.get().getActiveLanguageHistory().getLanguageSpokenByChild().getLanguageCode()).isEqualTo("EN");
        assertThat(retrievedEncounter.get().getActiveBirthHistory().getDeliveryType()).isEqualTo(DeliveryType.VAGINAL);
    }
}
