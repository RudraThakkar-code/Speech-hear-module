# Master Clinical Data Dictionary — v1.0

This document defines the single source of truth for the clinical data collected, analyzed, and managed within the application. It maps out the exact clinical information gathered throughout the patient journey, emphasizing the separation of **Raw Information**, **Structured Observation**, **Professional Interpretation**, and **AI-Generated Information**.

## Permission & Correction Global Rule
*Clinical records must not be destructively overwritten. Instead of unrestricted editing, modifications require a "Correction Request → Authorized Review → Approved Change → Audit Trail" workflow.*

## Global Metadata Layer (`DataRecord` Implementation Note)
*Implementation Note: Rather than forcing strict SQL table inheritance, the `DataRecord` fields below represent a **conceptual metadata layer** that must be embedded/supported by all clinical tables via a common audit/version mechanism.*

| Conceptual Field | Description | Data type | Required? | Who enters? | Data classification |
|---|---|---|---|---|---|
| `record_id` | Unique system identifier for the record | UUID | Yes | System | Auto-generated |
| `case_id` | Associated clinical case identifier | UUID | Yes | System | Auto-generated |
| `encounter_id` | The specific clinical event/session this record belongs to | UUID | Yes | System | Auto-generated |
| `created_by` / `created_at` | Provenance of record creation | UUID/Date | Yes | System | Auto-generated |
| `updated_by` / `updated_at` | Provenance of last modification | UUID/Date | No | System | Auto-generated |
| `data_provenance` | Patient, Parent, Therapist, Supervisor, Doctor, System | Enum | Yes | System | Auto-generated |
| `data_classification` | Raw, Observation, Interpretation, AI-Generated | Enum | Yes | System/User | Auto-generated |
| `verification_status` | Unverified, Verified | Enum | Yes | Clinician | Structured Observation |
| `interpretation_status` | Not Interpreted, Clinician Interpreted | Enum | Yes | Clinician | Professional Interpretation |
| `version` | Sequential version number for audit trailing | Integer | Yes | System | Auto-generated |

---

## 1. Longitudinal Hierarchy (Patient → Case → Encounter)

### 1A. Case
| Field | Description | Data type | Required? | Who enters? | Data classification |
|---|---|---|---|---|---|
| `case_id` | Unique identifier for a continuous episode of care | UUID | Yes | System | Auto-generated |
| `patient_id` | Link to the patient profile | UUID | Yes | System | Auto-generated |
| `case_status` | Active, Discharged, On Hold | Enum | Yes | Therapist/Supervisor | Structured Observation |
| `assigned_therapist_id` | Primary therapist handling the case | UUID | Yes | Admin/System | Auto-generated |
| `assigned_supervisor_id` | Supervisor overseeing this case | UUID | Yes | Admin/System | Auto-generated |

### 1B. Encounter
| Field | Description | Data type | Required? | Who enters? | Data classification |
|---|---|---|---|---|---|
| `encounter_id` | Unique ID for the clinical session | UUID | Yes | System | Auto-generated |
| `case_id` | Link to the overarching case | UUID | Yes | System | Auto-generated |
| `encounter_type` | Initial Evaluation, Therapy Session, Follow-up, Reassessment | Enum | Yes | Therapist | Structured Observation |
| `encounter_date_time` | When the session occurred | DateTime | Yes | System/Therapist | Auto-generated |
| `assessment_reason` | Chief Complaint, Screening, Initial Assessment, Reassessment, Follow-up, Doctor Referral, Therapy Progress Evaluation, Therapist Concern, Supervisor Request | Enum | Yes | Therapist/Supervisor | Structured Administrative/Workflow Data |

---

## 2. Patient Identity

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `patient_id` | Unique system identifier for the patient | UUID | Yes | Unique | Auto-generated | System |
| `legal_name` | Full legal name of the patient | Text | Yes | Length > 1 | Raw Information | Patient/Parent |
| `preferred_name` | Preferred or display name | Text | No | None | Raw Information | Patient/Parent |
| `date_of_birth` | Date of birth of the patient | Date | Yes | Past date | Raw Information | Patient/Parent |
| `age_years_calculated` | System-calculated age in years | Integer | Yes | >= 0 | Auto-generated | System |
| `age_months_calculated` | System-calculated remaining age in months | Integer | Yes | 0-11 | Auto-generated | System |
| `sex_at_birth` | Sex assigned at birth | Enum | Clinically Relevant | Valid enum | Raw Information | Patient/Parent |
| `gender_identity` | Self-identified gender | Enum/Text | No | None | Raw Information | Patient/Parent |

## 3. Registration

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `registration_date` | Date patient profile was created | DateTime | Yes | Valid DateTime | Auto-generated | System |
| `contact_number` | Primary contact number | Text | Yes | Valid format | Raw Information | Patient/Parent |
| `email_address` | Primary email address | Text | No | Valid format | Raw Information | Patient/Parent |
| `home_address` | Current residential address | Text | No | Length > 5 | Raw Information | Patient/Parent |

## 4. Chief Complaint

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `complaint_text` | Patient's/parent's primary concern in their own words | Text | Yes | Length > 5 | Raw Information | Patient/Parent |
| `complaint_audio_url` | Original recording of the patient/parent stating concern | Media URL | No | Valid audio | Raw Information | Patient/Parent |
| `onset_duration` | How long the concern has been noticed | Text | Yes | Not empty | Raw Information | Patient/Parent |
| `previous_consultation` | Has this been evaluated before? | Boolean | Yes | True/False | Raw Information | Patient/Parent |

## 5. Family History

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance | Dependency |
|---|---|---|---|---|---|---|---|
| `family_speech_history` | Known speech problems in immediate family | Boolean | Yes | True/False | Raw Information | Patient/Parent | None |
| `family_speech_details` | Details regarding familial speech problems | Text | No | If True | Raw Information | Patient/Parent | `family_speech_history` |
| `family_hearing_history` | Known hearing problems in immediate family | Boolean | Yes | True/False | Raw Information | Patient/Parent | None |
| `family_hearing_details` | Details regarding familial hearing problems | Text | No | If True | Raw Information | Patient/Parent | `family_hearing_history` |
| `other_relevant_family_hx` | Other developmental/medical family conditions | Text | No | None | Raw Information | Patient/Parent | None |

## 6. Birth History

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `pregnancy_complications` | Any complications during pregnancy | Boolean | Yes | True/False | Raw Information | Parent |
| `pregnancy_complication_details` | Details of pregnancy complications | Text | No | If True | Raw Information | Parent |
| `delivery_type` | Normal, Cesarean, Instrumental | Enum | Yes | Valid enum | Raw Information | Parent |
| `prematurity_status` | Full term or premature birth | Enum | Yes | Valid enum | Raw Information | Parent |
| `birth_weight` | Weight at birth (if known/relevant) | Float | No | Positive # | Raw Information | Parent |
| `nicu_admission` | Was the child admitted to NICU? | Boolean | Yes | True/False | Raw Information | Parent |
| `other_relevant_birth_hx` | Additional significant birth history details | Text | No | None | Raw Information | Parent |

## 7. Medical History

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `reported_conditions` | Pre-existing conditions reported by patient/parent | Text | No | None | Raw Information | Patient/Parent |
| `verified_diagnoses` | Medical diagnoses verified via clinical reports | List of Strings | No | None | Structured Observation | Clinician |
| `neurological_history` | History of seizures, trauma, neurological issues | Text | No | None | Raw Information | Patient/Parent |
| `current_medications` | Any relevant current medications | Text | No | None | Raw Information | Patient/Parent |
| `surgical_history` | History of relevant surgeries (e.g., cleft palate) | Text | No | None | Raw Information | Patient/Parent |
| `previous_therapy_history` | Received speech/language therapy before? | Boolean | Yes | True/False | Raw Information | Patient/Parent |
| `evidence_document_ids` | References to uploaded prior medical reports | UUID Array | No | Valid ref | Auto-generated | System |

## 8. Developmental History

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `milestone_sitting_age` | Age child sat without support (Months) | Integer | No | Positive # | Raw Information | Parent |
| `milestone_walking_age` | Age child walked independently (Months) | Integer | No | Positive # | Raw Information | Parent |
| `motor_milestones_status` | Typical, Delayed, Unknown | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `motor_milestones_details` | Concerns regarding motor milestones | Text | No | If Delayed | Raw Information | Parent |
| `feeding_difficulties` | History of chewing, swallowing, sucking issues | Boolean | Yes | True/False | Raw Information | Parent |
| `scholastic_history` | School performance/difficulties | Text | No | None | Raw Information | Parent |

## 9. Language History

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `language_spoken_by_child` | Primary language the child currently speaks | Enum | Yes | Valid enum | Raw Information | Parent/Therapist |
| `language_understood` | Languages the child comprehends | List of Enums | Yes | Valid enums | Raw Information | Parent/Therapist |
| `language_heard_at_home` | Primary languages spoken in the household | List of Enums | Yes | Valid enums | Raw Information | Parent |
| `language_exposure_details` | Caregivers' languages, age of exposure, frequency | Text | No | None | Raw Information | Parent/Therapist |
| `first_words_age` | Age first meaningful words spoken (Months) | Integer | No | Positive # | Raw Information | Parent |
| `phrase_speech_age` | Age 2-3 word phrases began (Months) | Integer | No | Positive # | Raw Information | Parent |
| `language_development_concerns` | Parental concerns regarding language | Text | No | None | Raw Information | Parent |


## 10. Hearing Screening (Software-assisted Preliminary Screening)
*Disclaimer: This is software-assisted preliminary screening; it is NOT diagnostic audiometry.*

### 10A. Hearing Screening Event
| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `screening_id` | Unique ID for the screening event | UUID | Yes | Unique | Auto-generated | System |
| `screening_date` | Date of the preliminary screening | Date | Yes | Past/current date | Raw Information | Therapist |
| `screening_environment` | Quiet Room, Sound-treated Room, Open Clinic | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `screening_device_used` | Headphone/device used | Text | Yes | None | Structured Observation | Therapist |
| `screening_outcome` | Derived outcome: No concern, Possible concern, Inconclusive, Unable to complete | Enum | Yes | Valid enum | Professional Interpretation | Therapist |
| `recommended_action` | No immediate action, Repeat screening, Diagnostic hearing evaluation, Clinician review | Enum | Yes | Valid enum | Professional Interpretation | Therapist |
| `existing_audiology_report_id` | Reference to definitive audiological report | UUID | No | Valid ref | Auto-generated | System |

### 10B. Hearing Screening Measurement (1-to-Many)
| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `measurement_id` | Unique ID for this specific trial | UUID | Yes | Unique | Auto-generated | System |
| `screening_id` | Link to the parent screening event | UUID | Yes | Valid ref | Auto-generated | System |
| `trial_order` | The sequence number of this measurement | Integer | Yes | Positive # | Auto-generated | System |
| `ear` | Left, Right, Bilateral | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `frequency_presented` | Frequency (Hz) (Protocol to be validated) | Integer | Yes | Positive # | Structured Observation | Therapist |
| `intensity_presented` | Signal intensity (dB) (Protocol to be validated) | Integer | Yes | Positive # | Structured Observation | Therapist |
| `response_method` | Button press, Hand raise, Verbal response, Clinician observation, Other | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `patient_response` | Consistent, Inconsistent, No Response | Enum | Yes | Valid enum | Structured Observation | Therapist |

## 11. Assessment Task Library & Assignments

### 11A. Assessment Task Library (Reference Object)
| Field | Description | Data type | Required? | Data classification |
|---|---|---|---|---|
| `task_id` | Unique ID of the reusable task | UUID | Yes | Auto-generated |
| `task_version` | Version number for this task definition | Integer | Yes | System (Dictionary) |
| `active` | Is this task still in use? | Boolean | Yes | System (Dictionary) |
| `created_at` | When the task was introduced | DateTime | Yes | System (Dictionary) |
| `retired_at` | When the task was removed from active use | DateTime | No | System (Dictionary) |
| `language` | Language of the task material | Enum | Yes | System (Dictionary) |
| `task_type` | Spontaneous, Reading, Picture Description, Target-word | Enum | Yes | System (Dictionary) |
| `instructions` | Instructions to give the patient | Text | Yes | System (Dictionary) |
| `prompt_material` | Text, question, or image URL presented | Text/URL | Yes | System (Dictionary) |

### 11B. Assessment Task Assignment
| Field | Description | Data type | Required? | Validation | Data classification |
|---|---|---|---|---|---|
| `assignment_id` | Unique ID mapping a task to an encounter | UUID | Yes | Unique | Auto-generated |
| `task_id` | Link to the library task | UUID | Yes | Valid ref | Auto-generated |
| `encounter_id` | Link to the encounter | UUID | Yes | Valid ref | Auto-generated |
| `assigned_by` | Therapist ID assigning the task | UUID | Yes | Valid ref | Auto-generated |
| `sequence_order` | Order the task should be performed in | Integer | Yes | Positive # | Structured Observation |
| `required` | Is this task mandatory? | Boolean | Yes | True/False | Structured Observation |
| `completion_status` | Pending, Attempted, Completed, Skipped | Enum | Yes | Valid enum | Structured Observation |

## 12. Speech Sampling (Task Attempt)

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `sample_id` | Unique ID for this recording attempt | UUID | Yes | Unique | Auto-generated | System |
| `assignment_id` | Link to `AssessmentTaskAssignment` | UUID | Yes | Valid ref | Auto-generated | System |
| `assessment_language` | Language the assessment was performed in | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `speaker_type` | Patient, Parent, Therapist, Multiple | Enum | Yes | Valid enum | Structured Observation | Therapist/System |
| `speaker_count` | Number of distinct speakers detected/observed | Integer | No | Positive # | Structured Observation | Therapist/System |
| `sample_duration_seconds` | Length of the speech sample | Integer | Yes | Positive # | Auto-generated | System |
| `sample_audio_url` | Location of the recorded audio file | Media URL | Yes | Valid audio | Raw Information | System |
| `recording_quality` | Good, Acceptable, Poor, Unusable | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `background_noise_level` | None, Low, High | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `analysis_eligibility` | Eligible, Not Eligible, Pending Review | Enum | Yes | Valid enum | Structured Observation | Therapist/System |
| `analysis_ineligibility_reason` | NOISE, INCOMPLETE, MULTIPLE_SPEAKERS, WRONG_LANGUAGE, INSUFFICIENT_DURATION, POOR_RECORDING, UNSUPPORTED_FORMAT, UNKNOWN | Enum | No | If Not Eligible | Structured Observation | Therapist/System |

## 13. Articulation Target Library (Reference Object)
| Field | Description | Data type | Required? | Data classification |
|---|---|---|---|---|
| `target_id` | Unique identifier for the articulation target | UUID | Yes | Auto-generated |
| `target_version` | Version number for this target definition | Integer | Yes | System (Dictionary) |
| `active` | Is this target still in use? | Boolean | Yes | System (Dictionary) |
| `created_at` | When the target was introduced | DateTime | Yes | System (Dictionary) |
| `retired_at` | When the target was removed from active use | DateTime | No | System (Dictionary) |
| `language` | Target language | Enum | Yes | System (Dictionary) |
| `target_type` | Isolated Sound, Syllable, Word, Phrase, Sentence, Connected Speech | Enum | Yes | System (Dictionary) |
| `phoneme` | Target phoneme | Text | Yes | System (Dictionary) |
| `word` | Word containing the phoneme (if applicable) | Text | No | System (Dictionary) |
| `position` | Initial, Medial, Final (if applicable) | Enum | No | System (Dictionary) |

## 14. Articulation Assessment (Production Attempt)

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `production_attempt_id`| Unique ID for this specific try | UUID | Yes | Unique | Auto-generated | System |
| `target_id` | Link to `SpeechTarget` | UUID | Yes | Valid ref | Auto-generated | System |
| `attempt_number` | 1st try, 2nd try, etc. | Integer | Yes | Positive # | Auto-generated | System |
| `production_accuracy` | Correct, Substitution, Omission, Distortion, Addition | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `produced_phoneme` | Exact sound produced (if substituted/distorted) | Text | No | If error | Structured Observation | Therapist |
| `clinician_observation`| Visual placement observations (lips/tongue) | Text | No | None | Structured Observation | Therapist |
| `production_audio_url` | Recording of this specific target try | Media URL | No | Valid audio | Raw Information | System |

## 15. Fluency Assessment

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `fluency_sample_id` | Link to the relevant speech sample | UUID | Yes | Valid ref | Auto-generated | System |
| `speech_rate_observation` | Clinician's perception (Typical, Fast, Slow) | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `speech_rate_value` | Objective measured rate (if validated) | Float | No | Positive # | AI-Generated/Therapist | AI/Therapist |
| `speech_rate_unit` | Syllables/min, Words/min | Enum | No | Valid enum | AI-Generated/Therapist | AI/Therapist |
| `total_syllables` | Total syllables in the analyzed sample | Integer | No | Positive # | Structured Observation | Therapist/System |
| `total_words` | Total words in the analyzed sample | Integer | No | Positive # | Structured Observation | Therapist/System |
| `repetition_count` | Number of repetitions (Sound/Syllable/Word) | Integer | No | Positive # | Structured Observation | Therapist |
| `prolongation_duration_est`| Estimated duration of longest prolongation (s) | Float | No | Positive # | Structured Observation | Therapist |
| `block_duration_est` | Estimated duration of longest block (s) | Float | No | Positive # | Structured Observation | Therapist |
| `atypical_pause_count` | Number of atypical pauses | Integer | No | Positive # | Structured Observation | Therapist |

## 16. Voice Assessment

### 16A. Clinical Voice Observation
| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `voice_sample_id` | Link to speech sample (Sustained vowel, etc.) | UUID | Yes | Valid ref | Auto-generated | System |
| `pitch_observation` | Clinician observation of pitch | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `loudness_observation` | Clinician observation of loudness | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `voice_quality` | Breathy, Harsh, Hoarse, Nasal, Typical | List(Enums) | Yes | Valid enums | Structured Observation | Therapist |
| `resonance_observation`| Typical, Hypernasal, Hyponasal, Mixed | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `voice_concern_flag` | Indicates need for further diagnostic voice pathway | Boolean | Yes | True/False | Structured Observation | Therapist |

### 16B. Voice Objective Measurement (Abstracted for Future AI/Acoustic Integration)
| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `measurement_id` | Unique ID for acoustic metric | UUID | Yes | Unique | Auto-generated | System |
| `voice_sample_id` | Link to the sample being measured | UUID | Yes | Valid ref | Auto-generated | System |
| `parameter_name` | Jitter, Shimmer, F0, etc. | Enum/Text | Yes | Valid param | AI-Generated / Device | System/AI |
| `value` | Measured numeric value | Float | Yes | None | AI-Generated / Device | System/AI |
| `unit` | Hz, %, dB, etc. | Text | Yes | None | AI-Generated / Device | System/AI |
| `measurement_source` | Clinician, Device, Software, AI | Enum | Yes | Valid enum | Structured Observation | System/AI/Therapist |
| `measurement_method` | The specific software/model used | Text | Yes | None | Structured Observation | System/AI/Therapist |

## 17. Video Observation
*Note: Video logs observed behavior; it does NOT formulate psychological diagnoses.*

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `video_sample_url` | Location of recorded video | Media URL | Yes | Valid video | Raw Information | System |
| `camera_position` | Frontal, Profile, Off-angle | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `video_context` | Assessment step context (e.g., Fluency task) | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `secondary_behavior_eye` | Blinking/eye movements observed | Boolean/Text| No | None | Structured Observation | Therapist |
| `secondary_behavior_facial`| Facial grimacing/movements observed | Boolean/Text| No | None | Structured Observation | Therapist |
| `secondary_behavior_limb` | Head/hand/limb movements observed | Boolean/Text| No | None | Structured Observation | Therapist |


## 18. Provisional Clinical Assessment

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `clinical_interpretation_id` | Unique ID for the provisional interpretation | UUID | Yes | Unique | Auto-generated | System |
| `primary_clinical_problem` | Articulation difficulty, Fluency concern, Language delay | List(Enums) | Yes | Valid enums | Professional Interpretation | Therapist/Supervisor |
| `evidence_summary` | Summary of observations supporting the interpretation | Text | Yes | None | Professional Interpretation | Therapist/Supervisor |
| `clinical_assessment_status` | Draft, Submitted, Under Review, Finalized | Enum | Yes | Valid enum | Auto-generated | System/Supervisor |
| `recommended_action` | Therapy, Further Assessment, Doctor Referral, Continue Monitoring, Insufficient Evidence/Unable to Conclude | Enum | Yes | Valid enum | Professional Interpretation | Therapist/Supervisor |

## 19. Therapy Goals

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `goal_id` | Unique identifier for the goal | UUID | Yes | Unique | Auto-generated | System |
| `goal_type` | Long-term, Short-term | Enum | Yes | Valid enum | Professional Interpretation | Therapist |
| `domain` | Articulation, Fluency, Voice, Language, Hearing | Enum | Yes | Valid enum | Professional Interpretation | Therapist |
| `description` | Specific measurable target description | Text | Yes | None | Professional Interpretation | Therapist |

### 19B. Goal Metrics (Abstracted)
| Field | Description | Data type | Required? | Validation | Data classification |
|---|---|---|---|---|---|
| `metric_id` | Unique identifier for a metric measurement | UUID | Yes | Unique | Auto-generated |
| `goal_id` | Link to the specific therapy goal | UUID | Yes | Valid ref | Auto-generated |
| `metric_type` | Baseline, Target, Progress Check | Enum | Yes | Valid enum | Structured Observation |
| `metric_name` | Accuracy, Disfluency count, Duration | Text | Yes | None | Structured Observation |
| `value` | Measured/Target amount | Float/Int | Yes | None | Structured Observation |
| `unit` | %, count, blocks/min, seconds | Text | Yes | None | Structured Observation |

## 20. Therapy Plan

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `plan_id` | Unique identifier for the plan | UUID | Yes | Unique | Auto-generated | System |
| `linked_goal_ids` | Short-term goals addressed in this plan | UUID Array | Yes | Valid refs | Auto-generated | System |
| `plan_start_date` | Effective start date of the plan | Date | Yes | Valid Date | Professional Interpretation | Therapist |
| `plan_end_date` | Anticipated/actual end date of the plan | Date | No | > Start Date | Professional Interpretation | Therapist |
| `planned_activities` | Description of clinical activities/strategies | Text | Yes | None | Professional Interpretation | Therapist |
| `frequency_value` | Suggested occurrences (e.g., 3) | Integer | Yes | Positive # | Professional Interpretation | Therapist |
| `frequency_unit` | Week, Month | Enum | Yes | Valid enum | Professional Interpretation | Therapist |
| `supervisor_approval_status` | Pending, Approved, Modify, Rejected | Enum | Yes | Valid enum | Professional Interpretation | Supervisor |

## 21. Therapy Session

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `session_id` | ID mapping to `encounter_id` | UUID | Yes | Unique | Auto-generated | System |
| `session_status` | Completed, Cancelled, Missed, Rescheduled | Enum | Yes | Valid enum | Structured Observation | Therapist |
| `addressed_goal_ids` | Goals worked on during this session | UUID Array | Yes | Valid refs | Structured Observation | Therapist |
| `activities_performed` | What actually happened during the session | Text | Yes | None | Structured Observation | Therapist |
| `patient_performance` | Observation of how the patient performed | Text | Yes | None | Structured Observation | Therapist |
| `homework_assigned` | Activities assigned for home practice | Text | No | None | Professional Interpretation | Therapist |

## 22. Home Practice

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `practice_id` | Unique ID for the assigned task | UUID | Yes | Unique | Auto-generated | System |
| `session_id` | Link to the session where this was assigned | UUID | Yes | Valid ref | Auto-generated | System |
| `assigned_at` | When the task was assigned | DateTime | Yes | Valid date | Auto-generated | System |
| `due_at` | When the task should be finished | DateTime | Yes | > Assigned Date | Professional Interpretation | Therapist |
| `completed_at` | When the task was actually submitted/finished | DateTime | No | Valid date | Raw Information | Parent/Patient |
| `task_description` | Instructions for parent/patient | Text | Yes | None | Professional Interpretation | Therapist |
| `completion_status` | Not Started, Partially Completed, Completed | Enum | Yes | Valid enum | Raw Information | Parent/Patient |
| `practice_media_url` | Optional video/audio uploaded by parent | Media URL | No | Valid format | Raw Information | Parent |

## 23. Follow-up

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `followup_id` | Link to a specific follow-up encounter | UUID | Yes | Unique | Auto-generated | System |
| `previous_recommendations_met` | Have prior instructions been followed? | Boolean | Yes | True/False | Structured Observation | Therapist |
| `current_status_summary` | Therapist's view on current trajectory | Text | Yes | None | Professional Interpretation | Therapist |

## 24. Progress Monitoring

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `report_id` | Unique ID for a progress check | UUID | Yes | Unique | Auto-generated | System |
| `goal_id` | The specific goal being measured | UUID | Yes | Valid ref | Auto-generated | System |
| `metric_id` | Link to the specific metric checkpoint powering this progress | UUID | Yes | Valid ref | Auto-generated | System |
| `progress_status` | Improved, Stable, Declined, Achieved, Insufficient Data | Enum | Yes | Valid enum | Professional Interpretation | Therapist |
| `therapist_progress_note` | Interpretation of the trajectory | Text | Yes | None | Professional Interpretation | Therapist |

## 25. Supervisor Review

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `review_id` | Unique identifier for a supervisor's review event | UUID | Yes | Unique | Auto-generated | System |
| `target_record_id` | Link to assessment, therapy plan, or session | UUID | Yes | Valid ref | Auto-generated | System |
| `review_status` | Pending, Completed | Enum | Yes | Valid enum | Structured Observation | Supervisor |
| `reviewed_at` | Timestamp when the review was finalized | DateTime | No | Valid date | Auto-generated | System |
| `supervisor_action` | Approve, Return for Correction, Modify | Enum | Yes | Valid enum | Professional Interpretation | Supervisor |
| `supervisor_comments` | Context for the action taken | Text | No | None | Professional Interpretation | Supervisor |

## 26. Clinical Rating

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `rating_id` | Unique identifier for a supervisor rating | UUID | Yes | Unique | Auto-generated | System |
| `therapist_id` | Link to the therapist being rated | UUID | Yes | Valid ref | Auto-generated | System |
| `rating_period_start` | Start date of the evaluation period/case | Date | Yes | Valid date | Auto-generated | System/Supervisor |
| `rating_period_end` | End date of the evaluation period/case | Date | Yes | > Start Date | Auto-generated | System/Supervisor |
| `assessment_reasoning_quality` | Score assessing clinical reasoning/documentation (1-5) | Integer | Yes | 1-5 | Professional Interpretation | Supervisor |
| `therapy_effectiveness_score` | Score assessing therapy execution (1-5) | Integer | Yes | 1-5 | Professional Interpretation | Supervisor |

## 27. Reports & Analytics

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `generated_report_id` | Unique ID for a compiled clinical report | UUID | Yes | Unique | Auto-generated | System |
| `report_type` | Baseline, Monthly Progress, Discharge Summary | Enum | Yes | Valid enum | Auto-generated | System |
| `report_content` | JSON aggregation of goals, sessions, and outcomes | JSON | Yes | Valid JSON | Auto-generated | System |

## 28. Notifications & Tasks

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `notification_id` | Unique identifier | UUID | Yes | Unique | Auto-generated | System |
| `recipient_id` | User receiving the alert | UUID | Yes | Valid ref | Auto-generated | System |
| `notification_type` | Pending Review, Correction Required | Enum | Yes | Valid enum | Auto-generated | System |

## 29. Granular Consent & Data Governance

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `consent_id` | Unique identifier for a consent agreement | UUID | Yes | Unique | Auto-generated | System |
| `patient_id` | Link to the patient | UUID | Yes | Valid ref | Auto-generated | System |
| `consent_data_collection` | Consent to collect baseline clinical data | Boolean | Yes | True/False | Raw Information | Parent/Patient |
| `consent_audio_video` | Consent to record/store clinical audio/video | Boolean | Yes | True/False | Raw Information | Parent/Patient |
| `consent_ai_analysis` | Consent to run clinical records through AI analysis | Boolean | Yes | True/False | Raw Information | Parent/Patient |
| `consent_ai_training` | Optional consent to use anonymized data for training | Enum | Yes | Granted, Denied, Not Asked, Withdrawn | Raw Information | Parent/Patient |
| `consent_signature_url` | Electronic signature or scanned consent form | URL | Yes | Valid format | Raw Information | Parent/Patient |

## 30. AI Analysis Metadata

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `ai_artifact_id` | Unique identifier linking AI output to record | UUID | Yes | Unique | Auto-generated | System |
| `source_record_id` | Link to audio, video, or text being analyzed | UUID | Yes | Valid ref | Auto-generated | System |
| `model_version` | The specific AI model version used | Text | Yes | None | Auto-generated | System (AI) |
| `analysis_type` | Speech Feature Extraction, Summarization | Enum | Yes | Valid enum | Auto-generated | System (AI) |
| `ai_generated_result` | The raw output string/JSON | JSON/Text | Yes | None | AI-Generated | System (AI) |
| `confidence_score` | Model's confidence (0.0-1.0) | Float | Yes | 0-1 | Auto-generated | System (AI) |
| `human_review_status` | Pending, Accepted, Rejected, Modified | Enum | Yes | Valid enum | Structured Observation | Supervisor/Therapist |
| `reviewed_by` | The ID of the human reviewing the AI output | UUID | No | Valid ref | Auto-generated | System |
| `reviewed_at` | Timestamp when the human finalized the review | DateTime | No | Valid date | Auto-generated | System |

## 31. System Audit Trail

| Field | Description | Data type | Required? | Validation | Data classification | Data provenance |
|---|---|---|---|---|---|---|
| `audit_log_id` | Global tracking identifier for state changes | UUID | Yes | Unique | Auto-generated | System |
| `table_name` | The database table/module modified | Text | Yes | Valid name | Auto-generated | System |
| `record_id` | The specific record updated | UUID | Yes | Valid ref | Auto-generated | System |
| `action_performed` | CREATE, UPDATE, SOFT_DELETE | Enum | Yes | Valid enum | Auto-generated | System |
| `changed_fields_snapshot` | JSON representation of the explicit diff/values changed | JSON | Yes | Valid JSON | Auto-generated | System |
| `actor_id` | The user or system initiating the action | UUID | Yes | Valid ref | Auto-generated | System |
