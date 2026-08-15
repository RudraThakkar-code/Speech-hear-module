# Clinical ER Data Model — v1.0

## 1. Purpose
This document translates Master Clinical Data Dictionary — v1.0 into a relational/entity model for the speech-language clinical management and supervision platform.

The model preserves the v1.0 separation between:
* **Reference Data** — reusable task and articulation libraries maintained by the system.
* **Clinical Data** — patient-specific case, encounter, assessment, therapy, supervision, consent, AI, and audit records.
* **System Data** — notifications and audit infrastructure.

The model also preserves the clinical information boundary between Raw Information, Structured Observation, Professional Interpretation, and AI-Generated Information.

---

## 2. Top-Level Domain Separation

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                         REFERENCE DATA                                  │
│                                                                         │
│  assessment_task_library     articulation_target_library              │
│              │                           │                              │
│              └──────────────┬────────────┘                              │
└─────────────────────────────┼───────────────────────────────────────────┘
                              │ referenced by
                              ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          CLINICAL DATA                                  │
│                                                                         │
│ Patient                                                                  │
│   │                                                                      │
│   └──< Case                                                              │
│         │                                                                │
│         ├──< History Domains (Versioned longitudinally)                  │
│         │                                                                │
│         └──< Encounter (References active History version)               │
│               ├──< AssessmentTaskAssignment >── Reference Task           │
│               │        └──< SpeechSample >── Media                       │
│               ├──< HearingScreening                                      │
│               │        └──< HearingScreeningMeasurement                  │
│               ├──< ArticulationProductionAttempt >── Target Library      │
│               ├──< FluencyAssessment >── SpeechSample                    │
│               ├──< VoiceAssessment >── SpeechSample                      │
│               │        └──< VoiceObjectiveMeasurement                    │
│               ├──< VideoObservation                                      │
│               ├──< ClinicalInterpretation                                 │
│               ├──< TherapySession                                         │
│               │        └──< HomePractice                                  │
│               ├──< FollowUp                                                │
│               └──< ProgressRecord                                          │
│                                                                            │
│ Case also owns/references: Goals, GoalMetrics, TherapyPlans,              │
│ SupervisorReviews, ClinicalRatings, Reports, Consents, AIArtifacts       │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                           SYSTEM DATA                                   │
│                                                                         │
│ Notifications             AuditLog                                      │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Global Conventions

### 3.1 Identifiers
* PKs use UUIDs.
* Foreign keys use UUIDs matching the referenced PK.
* Human-readable names are not identifiers.

### 3.2 Common audit/version support
For clinical tables that require it, the implementation should support:
```text
record_id
case_id
encounter_id (when encounter-scoped)
created_by
created_at
updated_by
updated_at
version
data_provenance
data_classification
verification_status
interpretation_status
```

### 3.3 Deletion policy
* **No physical DELETE** for clinical records.
* Use status/soft-delete/versioning where lifecycle changes are required.
* Audit every change.

---

## 4. Reference Data Entities

### 4.1 `AssessmentTask`
Reusable assessment task definition.

| Field | Type | Null | Key / Constraint | Enum / Notes |
|---|---|---|---|---|
| `task_id` | UUID | NO | PK | |
| `task_version` | INTEGER | NO | UNIQUE with `task_id` | Version of task definition |
| `active` | BOOLEAN | NO | | |
| `created_at` | TIMESTAMP | NO | | |
| `retired_at` | TIMESTAMP | YES | | |
| `language` | VARCHAR / FK | NO | FK to language reference if normalized | v1.0 uses enum conceptually |
| `task_type` | ENUM | NO | | `SPONTANEOUS`, `READING`, `PICTURE_DESCRIPTION`, `TARGET_WORD` |
| `instructions` | TEXT | NO | | |
| `prompt_material` | TEXT / URL | NO | | |

* **Unique constraints Recommended:** `UNIQUE(language, task_type, task_version)`
* **Delete behavior:** RESTRICT.

### 4.2 `ArticulationTarget`
Reusable articulation target definition.

| Field | Type | Null | Key / Constraint | Enum / Notes |
|---|---|---|---|---|
| `target_id` | UUID | NO | PK | |
| `target_version` | INTEGER | NO | | |
| `active` | BOOLEAN | NO | | |
| `created_at` | TIMESTAMP | NO | | |
| `retired_at` | TIMESTAMP | YES | | |
| `language` | VARCHAR / FK | NO | | |
| `target_type` | ENUM | NO | | `ISOLATED_SOUND`, `SYLLABLE`, `WORD`, `PHRASE`, `SENTENCE`, `CONNECTED_SPEECH` |
| `phoneme` | TEXT | NO | | |
| `word` | TEXT | YES | | Applicable when relevant |
| `position` | ENUM | YES | | `INITIAL`, `MEDIAL`, `FINAL` |

* **Unique constraints Recommended:** `UNIQUE(language, target_type, phoneme, word, position, target_version)`
* **Delete behavior:** RESTRICT.

---

## 5. Core Clinical Entities

### 5.1 `Patient`
Root identity entity.

| Field | Type | Null | Key / Constraint | Enum / Notes |
|---|---|---|---|---|
| `patient_id` | UUID | NO | PK | |
| `legal_name` | TEXT | NO | | |
| `preferred_name` | TEXT | YES | | |
| `date_of_birth` | DATE | NO | | |
| `age_years_calculated` | INTEGER | Derived | | Do not persist unless needed for reporting cache |
| `age_months_calculated`| INTEGER | Derived | | Derived from DOB |
| `sex_at_birth` | ENUM | YES | | Clinically Relevant; v1.0 says context-dependent |
| `gender_identity` | TEXT / ENUM | YES | | |

* **Delete behavior:** RESTRICT / soft-delete only.

### 5.2 `Case`
Continuous episode of care for a patient.

| Field | Type | Null | Key / Constraint | Enum / Notes |
|---|---|---|---|---|
| `case_id` | UUID | NO | PK | |
| `patient_id` | UUID | NO | FK → `Patient.patient_id` | |
| `case_status` | ENUM | NO | | `ACTIVE`, `DISCHARGED`, `ON_HOLD` |
| `assigned_therapist_id` | UUID | NO | FK → `User.user_id` | Primary therapist |
| `assigned_supervisor_id`| UUID | NO | FK → `User.user_id` | Supervisor |

* **Note for Future Expansion:** If multiple therapists/rotations are required, `assigned_therapist_id` and `assigned_supervisor_id` will be abstracted into a `CaseAssignment` entity (case_id, user_id, role, start/end). MVP uses direct FKs.
* **Delete behavior:** RESTRICT.

### 5.3 `Encounter`
A specific clinical event/session.

| Field | Type | Null | Key / Constraint | Enum / Notes |
|---|---|---|---|---|
| `encounter_id` | UUID | NO | PK | |
| `case_id` | UUID | NO | FK → `Case.case_id` | |
| `encounter_type` | ENUM | NO | | `INITIAL_EVALUATION`, `THERAPY_SESSION`, `FOLLOW_UP`, `REASSESSMENT` |
| `encounter_date_time` | TIMESTAMP | NO | | |
| `assessment_reason` | ENUM | NO | | `CHIEF_COMPLAINT`, `SCREENING`, `INITIAL_ASSESSMENT`, `REASSESSMENT`, `FOLLOW_UP`, `DOCTOR_REFERRAL`, `THERAPY_PROGRESS_EVALUATION`, `THERAPIST_CONCERN`, `SUPERVISOR_REQUEST` |

* **Delete behavior:** RESTRICT.

---

## 6. Intake / History Entities
History domains are scoped to the **Case** and **versioned** longitudinally. An encounter simply references the version active at the time of the session, avoiding repetitive parental input.

* `ChiefComplaint`
* `FamilyHistory`
* `BirthHistory`
* `MedicalHistory`
* `DevelopmentalHistory`
* `LanguageHistory`

### 6.1 Common relationship pattern
```text
Case 1 ─── N ChiefComplaint (Versions)
Case 1 ─── N FamilyHistory (Versions)
Case 1 ─── N BirthHistory (Versions)
Case 1 ─── N MedicalHistory (Versions)
Case 1 ─── N DevelopmentalHistory (Versions)
Case 1 ─── N LanguageHistory (Versions)
```
* **Delete behavior:** Versioned / Soft Delete only.

---

## 7. Hearing Domain

### 7.1 `HearingScreening`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `screening_id` | UUID | NO | PK | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | |
| `screening_date` | DATE | NO | | |
| `screening_environment` | ENUM | NO | | `QUIET_ROOM`, `SOUND_TREATED_ROOM`, `OPEN_CLINIC` |
| `screening_device_used` | TEXT | NO | | |
| `screening_outcome` | ENUM | NO | | `NO_CONCERN`, `POSSIBLE_CONCERN`, `INCONCLUSIVE`, `UNABLE_TO_COMPLETE` |
| `recommended_action` | ENUM | NO | | `NO_IMMEDIATE_ACTION`, `REPEAT_SCREENING`, `DIAGNOSTIC_HEARING_EVALUATION`, `CLINICIAN_REVIEW` |
| `existing_audiology_report_id` | UUID | YES| FK → `ClinicalDocument.document_id` | |

### 7.2 `HearingScreeningMeasurement`
| Field | Type | Null | Key / Constraint | Enum / Notes |
|---|---|---|---|---|
| `measurement_id` | UUID | NO | PK | |
| `screening_id` | UUID | NO | FK | |
| `trial_order` | INTEGER | NO | UNIQUE(`screening_id`,`trial_order`) | |
| `ear` | ENUM | NO | | `LEFT`, `RIGHT`, `BILATERAL` |
| `frequency_presented` | INTEGER | NO | | Protocol to be validated |
| `intensity_presented` | INTEGER | NO | | Protocol to be validated |
| `response_method` | ENUM | NO | | `BUTTON_PRESS`, `HAND_RAISE`, `VERBAL_RESPONSE`, `CLINICIAN_OBSERVATION`, `OTHER` |
| `patient_response` | ENUM | NO | | `CONSISTENT`, `INCONSISTENT`, `NO_RESPONSE` |

---

## 8. Assessment Task / Speech Sampling Domain

### 8.1 `AssessmentTaskAssignment`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `assignment_id` | UUID | NO | PK | |
| `task_id` | UUID | NO | FK → `AssessmentTask.task_id` | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | |
| `assigned_by` | UUID | NO | FK → `User.user_id` | |
| `sequence_order` | INTEGER | NO | UNIQUE(`encounter_id`,`sequence_order`) | |
| `required` | BOOLEAN | NO | | |
| `completion_status` | ENUM | NO | | `PENDING`, `ATTEMPTED`, `COMPLETED`, `SKIPPED` |

### 8.2 `SpeechSample`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `sample_id` | UUID | NO | PK | |
| `assignment_id` | UUID | NO | FK → `AssessmentTaskAssignment.assignment_id` | |
| `assessment_language`| VARCHAR / FK | NO| | |
| `speaker_type` | ENUM | NO | | `PATIENT`, `PARENT`, `THERAPIST`, `MULTIPLE` |
| `speaker_count` | INTEGER | YES| | |
| `sample_duration_seconds` | INTEGER | NO | | |
| `sample_audio_url` | TEXT | NO | | Object-storage reference |
| `recording_quality`| ENUM | NO | | `GOOD`, `ACCEPTABLE`, `POOR`, `UNUSABLE` |
| `background_noise_level`| ENUM| NO | | `NONE`, `LOW`, `HIGH` |
| `analysis_eligibility`| ENUM| NO | | `ELIGIBLE`, `NOT_ELIGIBLE`, `PENDING_REVIEW` |
| `analysis_ineligibility_reason`| ENUM| YES| | Required when `NOT_ELIGIBLE` |

---

## 9. Articulation Domain

### 9.1 `ArticulationProductionAttempt`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `production_attempt_id`| UUID | NO | PK | |
| `target_id` | UUID | NO | FK → `ArticulationTarget.target_id` | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | |
| `production_context_id`| UUID | YES| | Ensures grouping logic if assessed twice in one encounter |
| `attempt_number` | INTEGER| NO | UNIQUE(`encounter_id`,`target_id`,`attempt_number`) | |
| `production_accuracy`| ENUM | NO | | `CORRECT`, `SUBSTITUTION`, `OMISSION`, `DISTORTION`, `ADDITION` |
| `produced_phoneme` | TEXT | YES| | Required when applicable to error |
| `clinician_observation`| TEXT | YES| | |
| `production_audio_url` | TEXT | YES| | Object-storage reference |

---

## 10. Fluency Domain

### 10.1 `FluencyAssessment`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `fluency_assessment_id` | UUID | NO | PK | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | |
| `fluency_sample_id` | UUID | NO | FK → `SpeechSample.sample_id` | |
| `speech_rate_observation` | ENUM | NO | | `TYPICAL`, `FAST`, `SLOW` |
| `speech_rate_value` | DECIMAL| YES| | |
| `speech_rate_unit` | ENUM | YES| | `SYLLABLES_PER_MIN`, `WORDS_PER_MIN` |
| `total_syllables` | INTEGER| YES| | |
| `total_words` | INTEGER| YES| | |
| `repetition_count` | INTEGER| YES| | |
| `prolongation_duration_est`| DECIMAL| YES| | Seconds |
| `block_duration_est` | DECIMAL| YES| | Seconds |
| `atypical_pause_count`| INTEGER| YES| | |

---

## 11. Voice Domain

### 11.1 `VoiceAssessment`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `voice_assessment_id` | UUID | NO | PK | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | |
| `voice_sample_id` | UUID | NO | FK → `SpeechSample.sample_id` | |
| `pitch_observation` | ENUM | NO | | |
| `loudness_observation` | ENUM | NO | | |
| `voice_quality` | ENUM[] | NO | | `BREATHY`, `HARSH`, `HOARSE`, `NASAL`, `TYPICAL` |
| `resonance_observation` | ENUM | NO | | `TYPICAL`, `HYPERNASAL`, `HYPONASAL`, `MIXED` |
| `voice_concern_flag` | BOOLEAN| NO | | |

### 11.2 `VoiceObjectiveMeasurement`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `measurement_id` | UUID | NO | PK | |
| `voice_sample_id` | UUID | NO | FK → `SpeechSample.sample_id` | |
| `parameter_name` | TEXT / ENUM | NO | | |
| `value` | DECIMAL| NO | | |
| `unit` | TEXT | NO | | |
| `measurement_source` | ENUM | NO | | `CLINICIAN`, `DEVICE`, `SOFTWARE`, `AI` |
| `measurement_method` | TEXT | NO | | |

---

## 12. Video Domain

### 12.1 `VideoObservation`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `video_observation_id`| UUID | NO | PK | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | |
| `video_sample_url` | TEXT | NO | | Object-storage reference |
| `camera_position` | ENUM | NO | | `FRONTAL`, `PROFILE`, `OFF_ANGLE` |
| `video_context` | TEXT / ENUM | NO | | Assessment context |
| `secondary_behavior_eye` | TEXT / BOOLEAN | YES| | Observation only |
| `secondary_behavior_facial`| TEXT / BOOLEAN | YES| | Observation only |
| `secondary_behavior_limb` | TEXT / BOOLEAN | YES| | Observation only |

---

## 13. Provisional Clinical Assessment

### 13.1 `ClinicalInterpretation`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `clinical_interpretation_id`| UUID | NO | PK | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | |
| `primary_clinical_problem`| ENUM[] / join | NO | | Source-controlled values |
| `evidence_summary` | TEXT | NO | | |
| `clinical_assessment_status`| ENUM | NO | | `DRAFT`, `SUBMITTED`, `UNDER_REVIEW`, `FINALIZED` |
| `recommended_action` | ENUM | NO | | `THERAPY`, `FURTHER_ASSESSMENT`, `DOCTOR_REFERRAL`, `CONTINUE_MONITORING`, `INSUFFICIENT_EVIDENCE` |

---

## 14. Therapy Domain

### 14.1 `TherapyGoal`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `goal_id` | UUID | NO | PK | |
| `case_id` | UUID | NO | FK → `Case.case_id` | |
| `encounter_id` | UUID | YES| FK → `Encounter.encounter_id` | When created/defined during a specific encounter |
| `goal_type` | ENUM | NO | | `LONG_TERM`, `SHORT_TERM` |
| `domain` | ENUM | NO | | `ARTICULATION`, `FLUENCY`, `VOICE`, `LANGUAGE`, `HEARING` |
| `description` | TEXT | NO | | |

### 14.2 `GoalMetric`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `metric_id` | UUID | NO | PK | |
| `goal_id` | UUID | NO | FK → `TherapyGoal.goal_id` | |
| `metric_type` | ENUM | NO | | `BASELINE`, `TARGET`, `PROGRESS_CHECK` |
| `metric_name` | TEXT | NO | | |
| `value` | DECIMAL| NO | | |
| `unit` | TEXT | NO | | |

### 14.3 `TherapyPlan`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `plan_id` | UUID | NO | PK | |
| `case_id` | UUID | NO | FK → `Case.case_id` | |
| `plan_start_date` | DATE | NO | | |
| `plan_end_date` | DATE | YES| | Must be ≥ start date |
| `planned_activities` | TEXT | NO | | |
| `frequency_value` | INTEGER| NO | | |
| `frequency_unit` | ENUM | NO | | `WEEK`, `MONTH` |
| `supervisor_approval_status`| ENUM | NO | | `PENDING`, `APPROVED`, `MODIFY`, `REJECTED` |

### 14.4 `TherapySession`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `session_id` | UUID | NO | PK / FK → `Encounter` | if true 1:1 subtype is used |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id` | UNIQUE |
| `session_status` | ENUM | NO | | `COMPLETED`, `CANCELLED`, `MISSED`, `RESCHEDULED` |
| `activities_performed`| TEXT | NO | | |
| `patient_performance` | TEXT | NO | | |
| `homework_assigned` | TEXT | YES| | |

---

## 15. Home Practice

### 15.1 `HomePractice`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `practice_id` | UUID | NO | PK | |
| `session_id` | UUID | NO | FK → `TherapySession.session_id` | |
| `assigned_at` | TIMESTAMP| NO | | |
| `due_at` | TIMESTAMP| NO | | |
| `completed_at` | TIMESTAMP| YES| | |
| `task_description` | TEXT | NO | | |
| `completion_status` | ENUM | NO | | `NOT_STARTED`, `PARTIALLY_COMPLETED`, `COMPLETED` |
| `practice_media_url` | TEXT | YES| | Object-storage reference |

---

## 16. Follow-up & Progress

### 16.1 `FollowUp`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `followup_id` | UUID | NO | PK | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id`| UNIQUE |
| `previous_recommendations_met`| BOOLEAN| NO | | |
| `current_status_summary` | TEXT | NO | | |

### 16.2 `ProgressRecord`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `progress_record_id` | UUID | NO | PK | |
| `encounter_id` | UUID | NO | FK → `Encounter.encounter_id`| |
| `goal_id` | UUID | NO | FK → `TherapyGoal.goal_id` | |
| `metric_id` | UUID | NO | FK → `GoalMetric.metric_id` | |
| `progress_status` | ENUM | NO | | `IMPROVED`, `STABLE`, `DECLINED`, `ACHIEVED`, `INSUFFICIENT_DATA` |
| `therapist_progress_note`| TEXT | NO | | |

---

## 17. Supervisor Review & Clinical Rating

### 17.1 `SupervisorReview`
*Uses **Typed FK Constraints** to ensure referential integrity rather than generic polymorphism.*

| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `review_id` | UUID | NO | PK | |
| `reviewed_clinical_interpretation_id` | UUID | YES | FK | |
| `reviewed_therapy_plan_id` | UUID | YES | FK | |
| `reviewed_therapy_session_id` | UUID | YES | FK | |
| `review_status` | ENUM | NO | | `PENDING`, `COMPLETED` |
| `reviewed_at` | TIMESTAMP| YES| | |
| `supervisor_action` | ENUM | NO | | `APPROVE`, `RETURN_FOR_CORRECTION`, `MODIFY` |
| `supervisor_comments`| TEXT | YES| | |

* **Constraint:** `CHECK` constraint ensuring EXACTLY ONE of `reviewed_clinical_interpretation_id`, `reviewed_therapy_plan_id`, or `reviewed_therapy_session_id` is populated.

### 17.2 `ClinicalRating`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `rating_id` | UUID | NO | PK | |
| `therapist_id` | UUID | NO | FK → `User.user_id` | |
| `case_id` | UUID | YES| FK → `Case.case_id` | |
| `rating_period_start`| DATE | NO | | |
| `rating_period_end` | DATE | NO | | Must be ≥ start |
| `assessment_reasoning_quality`| INTEGER| NO | CHECK 1–5 | |
| `therapy_effectiveness_score` | INTEGER| NO | CHECK 1–5 | |

---

## 18. AI Artifacts

### 18.1 `AIArtifact`
*Uses **Typed FK Constraints** to ensure true source linkage.*

| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `ai_artifact_id` | UUID | NO | PK | |
| `source_speech_sample_id` | UUID | YES | FK | |
| `source_video_observation_id` | UUID | YES | FK | |
| `source_clinical_interpretation_id` | UUID | YES | FK | |
| `model_version` | TEXT | NO | | |
| `analysis_type` | ENUM | NO | | `SPEECH_FEATURE_EXTRACTION`, `SUMMARIZATION` |
| `ai_generated_result`| JSONB | NO | | |
| `confidence_score` | DECIMAL| NO | CHECK 0–1 | |
| `human_review_status`| ENUM | NO | | `PENDING`, `ACCEPTED`, `REJECTED`, `MODIFIED` |
| `reviewed_by` | UUID | YES| FK → `User.user_id` | |
| `reviewed_at` | TIMESTAMP| YES| | |

* **Constraint:** `CHECK` constraint ensuring EXACTLY ONE source FK is populated.

---

## 19. Governance & Reports

### 19.1 `Consent`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `consent_id` | UUID | NO | PK | |
| `patient_id` | UUID | NO | FK → `Patient.patient_id` | |
| `consent_data_collection` | BOOLEAN | NO | | |
| `consent_audio_video` | BOOLEAN | NO | | |
| `consent_ai_analysis` | BOOLEAN | NO | | |
| `consent_ai_training` | ENUM | NO | | `GRANTED`, `DENIED`, `NOT_ASKED`, `WITHDRAWN` |
| `consent_signature_url` | TEXT | NO | | |

### 19.2 `AuditLog`
| Field | Type | Null | Key / Constraint | Notes |
|---|---|---|---|---|
| `audit_log_id` | UUID | NO | PK | |
| `table_name` | TEXT | NO | | |
| `record_id` | UUID | NO | Generic record reference | |
| `action_performed` | ENUM | NO | | `CREATE`, `UPDATE`, `SOFT_DELETE` |
| `changed_fields_snapshot`| JSONB | NO | Explicit diff representation | |
| `actor_id` | UUID | NO | FK → `User.user_id` | |
| `occurred_at` | TIMESTAMP| NO | | |

---

## 20. Implementation Notes Summarized
* **History Scope:** Abstracted to `Case` level with explicit versions, heavily preventing encounter-level repetition.
* **Media / Auth / Identity:** To be implemented respectively via generic object storage (S3) and robust external auth providers outside PostgreSQL.

---

## 35. Doctor Portal & Clinical Collaboration (Phase 18)

### 35.1 `DoctorCaseAssignment`
Tracks cases referred to a medical doctor/clinician for review.
* **PK**: `assignment_id` (UUID)
* **FK**: `case_id` (UUID, NOT NULL) -> Ref: `ClinicalCase`
* **FK**: `doctor_id` (UUID, NOT NULL) -> Ref: `User`
* **Fields**:
  * `role` (VARCHAR, NOT NULL)
  * `assigned_at` (TIMESTAMP, NOT NULL)
  * `ended_at` (TIMESTAMP, NULL)
  * `status` (VARCHAR, NOT NULL)

### 35.2 `ClinicalDiscussion`
Auditable case-oriented professional discussion threads.
* **PK**: `discussion_id` (UUID)
* **FK**: `case_id` (UUID, NOT NULL) -> Ref: `ClinicalCase`
* **FK**: `encounter_id` (UUID, NULL) -> Ref: `Encounter`
* **Fields**:
  * `topic` (TEXT, NOT NULL)
  * `status` (ENUM, NOT NULL) -> `OPEN`, `RESOLVED`, `CLOSED`
  * `priority` (ENUM, NOT NULL) -> `LOW`, `ROUTINE`, `URGENT`
  * `created_by` (UUID, NOT NULL)
  * `created_at` (TIMESTAMP, NOT NULL)

### 35.3 `ClinicalDiscussionMessage`
* **PK**: `message_id` (UUID)
* **FK**: `discussion_id` (UUID, NOT NULL) -> Ref: `ClinicalDiscussion`
* **FK**: `author_id` (UUID, NOT NULL) -> Ref: `User`
* **FK**: `linked_document_id` (UUID, NULL) -> Ref: `ClinicalDocument`
* **Fields**:
  * `message_text` (TEXT, NOT NULL)
  * `linked_record_id` (UUID, NULL) -- Polymorphic fallback

### 35.4 `DoctorRecommendation`
Discrete entity distinguishing explicit medical recommendations/diagnoses from provisional speech therapies.
* **PK**: `recommendation_id` (UUID)
* **FK**: `case_id` (UUID, NOT NULL) -> Ref: `ClinicalCase`
* **FK**: `doctor_id` (UUID, NOT NULL) -> Ref: `User`
* **FK**: `discussion_id` (UUID, NULL) -> Ref: `ClinicalDiscussion`
* **Fields**:
  * `recommendation_text` (TEXT, NOT NULL)
  * `medical_diagnosis` (TEXT, NULL)
