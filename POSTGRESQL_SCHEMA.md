# Clinical Database PostgreSQL Schema

This document defines the raw executable PostgreSQL DDL mapping directly from the ER Data Model (v1.0) and the Master Clinical Data Dictionary (v1.0).

---

## 1. Extensions
```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;
```

## 2. Enums
```sql
-- Identity Enums
CREATE TYPE user_role AS ENUM ('PATIENT', 'PARENT_GUARDIAN', 'THERAPIST', 'SUPERVISOR', 'DOCTOR', 'ADMIN', 'SYSTEM');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED');

-- Case / Encounter Lifecycle
CREATE TYPE case_status AS ENUM ('ACTIVE', 'DISCHARGED', 'ON_HOLD');
CREATE TYPE encounter_type AS ENUM ('INITIAL_EVALUATION', 'THERAPY_SESSION', 'FOLLOW_UP', 'REASSESSMENT');
CREATE TYPE assessment_reason AS ENUM ('CHIEF_COMPLAINT', 'SCREENING', 'INITIAL_ASSESSMENT', 'REASSESSMENT', 'FOLLOW_UP', 'DOCTOR_REFERRAL', 'THERAPY_PROGRESS_EVALUATION', 'THERAPIST_CONCERN', 'SUPERVISOR_REQUEST');
CREATE TYPE sex_at_birth AS ENUM ('MALE', 'FEMALE', 'INTERSEX', 'UNDISCLOSED');

-- Reference Data Enums
CREATE TYPE task_type AS ENUM ('SPONTANEOUS', 'READING', 'PICTURE_DESCRIPTION', 'TARGET_WORD');
CREATE TYPE target_type AS ENUM ('ISOLATED_SOUND', 'SYLLABLE', 'WORD', 'PHRASE', 'SENTENCE', 'CONNECTED_SPEECH');
CREATE TYPE target_position AS ENUM ('INITIAL', 'MEDIAL', 'FINAL');

-- Assessment Enums
CREATE TYPE assignment_status AS ENUM ('PENDING', 'ATTEMPTED', 'COMPLETED', 'SKIPPED');
CREATE TYPE speaker_type AS ENUM ('PATIENT', 'PARENT', 'THERAPIST', 'MULTIPLE');
CREATE TYPE recording_quality AS ENUM ('GOOD', 'ACCEPTABLE', 'POOR', 'UNUSABLE');
CREATE TYPE noise_level AS ENUM ('NONE', 'LOW', 'HIGH');
CREATE TYPE analysis_eligibility AS ENUM ('ELIGIBLE', 'NOT_ELIGIBLE', 'PENDING_REVIEW');
CREATE TYPE analysis_ineligibility_reason AS ENUM ('NOISE', 'INCOMPLETE', 'MULTIPLE_SPEAKERS', 'WRONG_LANGUAGE', 'INSUFFICIENT_DURATION', 'POOR_RECORDING', 'UNSUPPORTED_FORMAT', 'UNKNOWN');
CREATE TYPE screening_environment AS ENUM ('QUIET_ROOM', 'SOUND_TREATED_ROOM', 'OPEN_CLINIC');
CREATE TYPE screening_outcome AS ENUM ('NO_CONCERN', 'POSSIBLE_CONCERN', 'INCONCLUSIVE', 'UNABLE_TO_COMPLETE');
CREATE TYPE clinical_recommended_action AS ENUM ('THERAPY', 'FURTHER_ASSESSMENT', 'DOCTOR_REFERRAL', 'CONTINUE_MONITORING', 'INSUFFICIENT_EVIDENCE');
CREATE TYPE hearing_recommended_action AS ENUM ('NO_IMMEDIATE_ACTION', 'REPEAT_SCREENING', 'DIAGNOSTIC_HEARING_EVALUATION', 'CLINICIAN_REVIEW');
CREATE TYPE ear_type AS ENUM ('LEFT', 'RIGHT', 'BILATERAL');
CREATE TYPE response_method AS ENUM ('BUTTON_PRESS', 'HAND_RAISE', 'VERBAL_RESPONSE', 'CLINICIAN_OBSERVATION', 'OTHER');
CREATE TYPE patient_response AS ENUM ('CONSISTENT', 'INCONSISTENT', 'NO_RESPONSE');
CREATE TYPE articulation_accuracy AS ENUM ('CORRECT', 'SUBSTITUTION', 'OMISSION', 'DISTORTION', 'ADDITION');
CREATE TYPE speech_rate_observation AS ENUM ('TYPICAL', 'FAST', 'SLOW');
CREATE TYPE speech_rate_unit AS ENUM ('SYLLABLES_PER_MIN', 'WORDS_PER_MIN');

CREATE TYPE pitch_observation AS ENUM ('TYPICAL', 'TOO_HIGH', 'TOO_LOW');
CREATE TYPE loudness_observation AS ENUM ('TYPICAL', 'TOO_LOUD', 'TOO_SOFT');
CREATE TYPE resonance_observation AS ENUM ('TYPICAL', 'HYPERNASAL', 'HYPONASAL', 'MIXED');
CREATE TYPE voice_quality AS ENUM ('BREATHY', 'HARSH', 'HOARSE', 'NASAL', 'TYPICAL');

CREATE TYPE measurement_source AS ENUM ('CLINICIAN', 'DEVICE', 'SOFTWARE', 'AI');
CREATE TYPE camera_position AS ENUM ('FRONTAL', 'PROFILE', 'OFF_ANGLE');

-- Interpretation & Therapy Enums
CREATE TYPE clinical_assessment_status AS ENUM ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'FINALIZED');
CREATE TYPE goal_type AS ENUM ('LONG_TERM', 'SHORT_TERM');
CREATE TYPE therapy_domain AS ENUM ('ARTICULATION', 'FLUENCY', 'VOICE', 'LANGUAGE', 'HEARING');
CREATE TYPE metric_type AS ENUM ('BASELINE', 'TARGET', 'PROGRESS_CHECK');
CREATE TYPE frequency_unit AS ENUM ('WEEK', 'MONTH');
CREATE TYPE supervisor_approval_status AS ENUM ('PENDING', 'APPROVED', 'MODIFY', 'REJECTED');
CREATE TYPE session_status AS ENUM ('COMPLETED', 'CANCELLED', 'MISSED', 'RESCHEDULED');
CREATE TYPE completion_status AS ENUM ('NOT_STARTED', 'PARTIALLY_COMPLETED', 'COMPLETED');
CREATE TYPE progress_status AS ENUM ('IMPROVED', 'STABLE', 'DECLINED', 'ACHIEVED', 'INSUFFICIENT_DATA');
CREATE TYPE supervisor_review_status AS ENUM ('PENDING', 'COMPLETED');
CREATE TYPE supervisor_action AS ENUM ('APPROVE', 'RETURN_FOR_CORRECTION', 'MODIFY');
CREATE TYPE report_type AS ENUM ('BASELINE', 'MONTHLY_PROGRESS', 'DISCHARGE_SUMMARY');
CREATE TYPE notification_type AS ENUM ('PENDING_REVIEW', 'CORRECTION_REQUIRED');
CREATE TYPE notification_status AS ENUM ('UNREAD', 'READ', 'DISMISSED');

-- AI / Governance / Audit
CREATE TYPE ai_analysis_type AS ENUM ('SPEECH_FEATURE_EXTRACTION', 'SUMMARIZATION');
CREATE TYPE human_review_status AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'MODIFIED');
CREATE TYPE consent_ai_training AS ENUM ('GRANTED', 'DENIED', 'NOT_ASKED', 'WITHDRAWN');
CREATE TYPE audit_action AS ENUM ('CREATE', 'UPDATE', 'SOFT_DELETE');
CREATE TYPE verification_status AS ENUM ('UNVERIFIED', 'VERIFIED');
CREATE TYPE interpretation_status AS ENUM ('NOT_INTERPRETED', 'CLINICIAN_INTERPRETED');
CREATE TYPE data_classification AS ENUM ('RAW', 'OBSERVATION', 'INTERPRETATION', 'AI_GENERATED');
CREATE TYPE data_provenance AS ENUM ('PATIENT', 'PARENT', 'THERAPIST', 'SUPERVISOR', 'DOCTOR', 'SYSTEM');
```

## 3. User / Identity
```sql
CREATE TABLE "users" (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role user_role NOT NULL,
    name TEXT NOT NULL,
    email TEXT,
    status user_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

## 4. Reference Tables
```sql
CREATE TABLE language_reference (
    language_code VARCHAR(10) PRIMARY KEY,
    language_name TEXT NOT NULL
);

CREATE TABLE clinical_problem_reference (
    problem_code VARCHAR(50) PRIMARY KEY,
    problem_name TEXT NOT NULL
);

CREATE TABLE clinical_document (
    document_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL, -- FK constraint applied after patient creation
    case_id UUID,             -- FK constraint applied after case creation
    uploaded_by UUID NOT NULL REFERENCES "users"(user_id),
    storage_url TEXT NOT NULL,
    mime_type TEXT,
    file_name TEXT,
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    classification TEXT
);

CREATE TABLE assessment_task_library (
    task_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    task_version INT NOT NULL,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    language_code VARCHAR(10) NOT NULL REFERENCES language_reference(language_code),
    task_type task_type NOT NULL,
    instructions TEXT NOT NULL,
    prompt_material TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    retired_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_assessment_task UNIQUE (language_code, task_type, task_version)
);

CREATE TABLE articulation_target_library (
    target_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    target_version INT NOT NULL,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    language_code VARCHAR(10) NOT NULL REFERENCES language_reference(language_code),
    target_type target_type NOT NULL,
    phoneme TEXT NOT NULL,
    word TEXT,
    position target_position,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    retired_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_articulation_target UNIQUE (language_code, target_type, phoneme, word, position, target_version)
);
```

## 5. Patient
```sql
CREATE TABLE patient (
    patient_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    legal_name TEXT NOT NULL,
    preferred_name TEXT,
    date_of_birth DATE NOT NULL,
    sex_at_birth sex_at_birth,
    gender_identity TEXT,
    contact_number TEXT NOT NULL,
    email_address TEXT,
    home_address TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    data_provenance data_provenance,
    data_classification data_classification,
    deleted_at TIMESTAMP WITH TIME ZONE
);

ALTER TABLE clinical_document ADD CONSTRAINT fk_document_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id);
```

## 6. Case
```sql
CREATE TABLE clinical_case (
    case_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patient(patient_id) ON DELETE RESTRICT,
    case_status case_status NOT NULL,
    assigned_therapist_id UUID NOT NULL REFERENCES "users"(user_id),
    assigned_supervisor_id UUID NOT NULL REFERENCES "users"(user_id),

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

ALTER TABLE clinical_document ADD CONSTRAINT fk_document_case FOREIGN KEY (case_id) REFERENCES clinical_case(case_id);
```

## 7. History / Version Tables (Case-scoped)
```sql
CREATE TABLE chief_complaint (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    complaint_text TEXT NOT NULL,
    complaint_audio_url TEXT,
    onset_duration TEXT NOT NULL,
    previous_consultation BOOLEAN NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    data_provenance data_provenance,
    deleted_at TIMESTAMP WITH TIME ZONE
);
CREATE UNIQUE INDEX uq_current_chief_complaint ON chief_complaint(case_id) WHERE is_current = TRUE;

CREATE TABLE family_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    family_speech_history BOOLEAN NOT NULL,
    family_speech_details TEXT,
    family_hearing_history BOOLEAN NOT NULL,
    family_hearing_details TEXT,
    other_relevant_family_hx TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    data_provenance data_provenance,
    verification_status verification_status,
    deleted_at TIMESTAMP WITH TIME ZONE
);
CREATE UNIQUE INDEX uq_current_family_history ON family_history(case_id) WHERE is_current = TRUE;

CREATE TABLE birth_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    pregnancy_complications BOOLEAN NOT NULL,
    pregnancy_complication_details TEXT,
    delivery_type TEXT NOT NULL,
    prematurity_status TEXT NOT NULL,
    birth_weight DECIMAL,
    nicu_admission BOOLEAN NOT NULL,
    other_relevant_birth_hx TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    data_provenance data_provenance,
    deleted_at TIMESTAMP WITH TIME ZONE
);
CREATE UNIQUE INDEX uq_current_birth_history ON birth_history(case_id) WHERE is_current = TRUE;

CREATE TABLE medical_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    reported_conditions TEXT,
    neurological_history TEXT,
    current_medications TEXT,
    surgical_history TEXT,
    previous_therapy_history BOOLEAN NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    data_provenance data_provenance,
    verification_status verification_status,
    deleted_at TIMESTAMP WITH TIME ZONE
);
CREATE UNIQUE INDEX uq_current_medical_history ON medical_history(case_id) WHERE is_current = TRUE;

CREATE TABLE verified_diagnosis (
    history_id UUID NOT NULL REFERENCES medical_history(history_id) ON DELETE CASCADE,
    diagnosis_code VARCHAR(50) NOT NULL REFERENCES clinical_problem_reference(problem_code),
    PRIMARY KEY(history_id, diagnosis_code)
);

CREATE TABLE medical_history_document (
    history_id UUID NOT NULL REFERENCES medical_history(history_id) ON DELETE CASCADE,
    document_id UUID NOT NULL REFERENCES clinical_document(document_id),
    PRIMARY KEY(history_id, document_id)
);

CREATE TABLE developmental_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    milestone_sitting_age INT,
    milestone_walking_age INT,
    motor_milestones_status TEXT NOT NULL,
    motor_milestones_details TEXT,
    feeding_difficulties BOOLEAN NOT NULL,
    scholastic_history TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);
CREATE UNIQUE INDEX uq_current_developmental_history ON developmental_history(case_id) WHERE is_current = TRUE;

CREATE TABLE language_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    language_spoken_by_child VARCHAR(10) NOT NULL REFERENCES language_reference(language_code),
    language_exposure_details TEXT,
    first_words_age INT,
    phrase_speech_age INT,
    language_development_concerns TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);
CREATE UNIQUE INDEX uq_current_language_history ON language_history(case_id) WHERE is_current = TRUE;

CREATE TABLE language_history_understood (
    history_id UUID NOT NULL REFERENCES language_history(history_id) ON DELETE CASCADE,
    language_code VARCHAR(10) NOT NULL REFERENCES language_reference(language_code),
    PRIMARY KEY(history_id, language_code)
);

CREATE TABLE language_history_heard_at_home (
    history_id UUID NOT NULL REFERENCES language_history(history_id) ON DELETE CASCADE,
    language_code VARCHAR(10) NOT NULL REFERENCES language_reference(language_code),
    PRIMARY KEY(history_id, language_code)
);
```

## 8. Encounter
```sql
CREATE TABLE encounter (
    encounter_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id) ON DELETE RESTRICT,
    encounter_type encounter_type NOT NULL,
    encounter_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    assessment_reason assessment_reason NOT NULL,

    -- Link to the active version of history domains at the time of the encounter
    active_chief_complaint_id UUID REFERENCES chief_complaint(history_id),
    active_family_history_id UUID REFERENCES family_history(history_id),
    active_birth_history_id UUID REFERENCES birth_history(history_id),
    active_medical_history_id UUID REFERENCES medical_history(history_id),
    active_developmental_history_id UUID REFERENCES developmental_history(history_id),
    active_language_history_id UUID REFERENCES language_history(history_id),

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);
```

## 9. Assessment Tables
```sql
CREATE TABLE hearing_screening (
    screening_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    screening_date DATE NOT NULL,
    screening_environment screening_environment NOT NULL,
    screening_device_used TEXT NOT NULL,
    screening_outcome screening_outcome NOT NULL,
    recommended_action hearing_recommended_action NOT NULL,
    existing_audiology_report_id UUID REFERENCES clinical_document(document_id),

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE hearing_screening_measurement (
    measurement_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    screening_id UUID NOT NULL REFERENCES hearing_screening(screening_id) ON DELETE CASCADE,
    trial_order INT NOT NULL,
    ear ear_type NOT NULL,
    frequency_presented INT NOT NULL,
    intensity_presented INT NOT NULL,
    response_method response_method NOT NULL,
    patient_response patient_response NOT NULL,

    CONSTRAINT uq_hearing_measurement UNIQUE(screening_id, trial_order)
);

CREATE TABLE assessment_task_assignment (
    assignment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    task_id UUID NOT NULL REFERENCES assessment_task_library(task_id),
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    assigned_by UUID NOT NULL REFERENCES "users"(user_id),
    sequence_order INT NOT NULL,
    required BOOLEAN NOT NULL,
    completion_status assignment_status NOT NULL,

    CONSTRAINT uq_task_assignment_sequence UNIQUE(encounter_id, sequence_order)
);

CREATE TABLE speech_sample (
    sample_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    assignment_id UUID NOT NULL REFERENCES assessment_task_assignment(assignment_id),
    assessment_language_code VARCHAR(10) NOT NULL REFERENCES language_reference(language_code),
    speaker_type speaker_type NOT NULL,
    speaker_count INT,
    sample_duration_seconds INT NOT NULL,
    sample_audio_url TEXT NOT NULL,
    recording_quality recording_quality NOT NULL,
    background_noise_level noise_level NOT NULL,
    analysis_eligibility analysis_eligibility NOT NULL,
    analysis_ineligibility_reason analysis_ineligibility_reason,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE articulation_assessment_context (
    context_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    context_description TEXT NOT NULL
);

CREATE TABLE articulation_production_attempt (
    production_attempt_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    target_id UUID NOT NULL REFERENCES articulation_target_library(target_id),
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    production_context_id UUID REFERENCES articulation_assessment_context(context_id),
    attempt_number INT NOT NULL,
    production_accuracy articulation_accuracy NOT NULL,
    produced_phoneme TEXT,
    clinician_observation TEXT,
    production_audio_url TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_articulation_attempt UNIQUE(encounter_id, target_id, attempt_number)
);

CREATE TABLE fluency_assessment (
    fluency_assessment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    fluency_sample_id UUID NOT NULL REFERENCES speech_sample(sample_id),
    speech_rate_observation speech_rate_observation NOT NULL,
    speech_rate_value DECIMAL,
    speech_rate_unit speech_rate_unit,
    total_syllables INT,
    total_words INT,
    repetition_count INT,
    prolongation_duration_est DECIMAL,
    block_duration_est DECIMAL,
    atypical_pause_count INT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_fluency_sample UNIQUE(fluency_sample_id)
);

CREATE TABLE voice_assessment (
    voice_assessment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    voice_sample_id UUID NOT NULL REFERENCES speech_sample(sample_id),
    pitch_observation pitch_observation NOT NULL,
    loudness_observation loudness_observation NOT NULL,
    voice_quality voice_quality[] NOT NULL,
    resonance_observation resonance_observation NOT NULL,
    voice_concern_flag BOOLEAN NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE voice_objective_measurement (
    measurement_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    voice_sample_id UUID NOT NULL REFERENCES speech_sample(sample_id),
    parameter_name TEXT NOT NULL,
    value DECIMAL NOT NULL,
    unit TEXT NOT NULL,
    measurement_source measurement_source NOT NULL,
    measurement_method TEXT NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE video_observation (
    video_observation_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    video_sample_url TEXT NOT NULL,
    camera_position camera_position NOT NULL,
    video_context TEXT NOT NULL,
    secondary_behavior_eye TEXT,
    secondary_behavior_facial TEXT,
    secondary_behavior_limb TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE clinical_interpretation (
    clinical_interpretation_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    evidence_summary TEXT NOT NULL,
    clinical_assessment_status clinical_assessment_status NOT NULL,
    recommended_action clinical_recommended_action NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE clinical_interpretation_problem (
    clinical_interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id) ON DELETE CASCADE,
    problem_code VARCHAR(50) NOT NULL REFERENCES clinical_problem_reference(problem_code),
    PRIMARY KEY(clinical_interpretation_id, problem_code)
);
```

## 10. Therapy Tables
```sql
CREATE TABLE therapy_goal (
    goal_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    encounter_id UUID REFERENCES encounter(encounter_id),
    goal_type goal_type NOT NULL,
    domain therapy_domain NOT NULL,
    description TEXT NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE goal_metric (
    metric_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    goal_id UUID NOT NULL REFERENCES therapy_goal(goal_id) ON DELETE CASCADE,
    metric_type metric_type NOT NULL,
    metric_name TEXT NOT NULL,
    value DECIMAL NOT NULL,
    unit TEXT NOT NULL
);

CREATE TABLE therapy_plan (
    plan_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    plan_start_date DATE NOT NULL,
    plan_end_date DATE CHECK (plan_end_date >= plan_start_date),
    planned_activities TEXT NOT NULL,
    frequency_value INT NOT NULL,
    frequency_unit frequency_unit NOT NULL,
    supervisor_approval_status supervisor_approval_status NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE therapy_plan_goal (
    plan_id UUID NOT NULL REFERENCES therapy_plan(plan_id),
    goal_id UUID NOT NULL REFERENCES therapy_goal(goal_id),
    PRIMARY KEY (plan_id, goal_id)
);

CREATE TABLE therapy_session (
    session_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    session_status session_status NOT NULL,
    activities_performed TEXT NOT NULL,
    patient_performance TEXT NOT NULL,
    homework_assigned TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_session_encounter UNIQUE(encounter_id)
);

CREATE TABLE therapy_session_goal (
    session_id UUID NOT NULL REFERENCES therapy_session(session_id),
    goal_id UUID NOT NULL REFERENCES therapy_goal(goal_id),
    PRIMARY KEY (session_id, goal_id)
);

CREATE TABLE home_practice (
    practice_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES therapy_session(session_id),
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL,
    due_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    task_description TEXT NOT NULL,
    completion_status completion_status NOT NULL,
    practice_media_url TEXT,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE follow_up (
    followup_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    previous_recommendations_met BOOLEAN NOT NULL,
    current_status_summary TEXT NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_followup_encounter UNIQUE(encounter_id)
);

CREATE TABLE progress_record (
    progress_record_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES encounter(encounter_id),
    goal_id UUID NOT NULL REFERENCES therapy_goal(goal_id),
    metric_id UUID NOT NULL REFERENCES goal_metric(metric_id),
    progress_status progress_status NOT NULL,
    therapist_progress_note TEXT NOT NULL,

    -- Audit Base Metadata
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_by UUID REFERENCES "users"(user_id),
    updated_at TIMESTAMP WITH TIME ZONE,
    version INT DEFAULT 1 NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);
```

## 11. Supervision
```sql
CREATE TABLE supervisor_review (
    review_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    supervisor_id UUID NOT NULL REFERENCES "users"(user_id),
    reviewed_clinical_interpretation_id UUID REFERENCES clinical_interpretation(clinical_interpretation_id),
    reviewed_therapy_plan_id UUID REFERENCES therapy_plan(plan_id),
    reviewed_therapy_session_id UUID REFERENCES therapy_session(session_id),
    review_status supervisor_review_status NOT NULL,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    supervisor_action supervisor_action NOT NULL,
    supervisor_comments TEXT,

    CONSTRAINT chk_exactly_one_target CHECK (
        (reviewed_clinical_interpretation_id IS NOT NULL)::int +
        (reviewed_therapy_plan_id IS NOT NULL)::int +
        (reviewed_therapy_session_id IS NOT NULL)::int = 1
    )
);

CREATE TABLE clinical_rating (
    rating_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    supervisor_id UUID NOT NULL REFERENCES "users"(user_id),
    therapist_id UUID NOT NULL REFERENCES "users"(user_id),
    case_id UUID REFERENCES clinical_case(case_id),
    rating_period_start DATE NOT NULL,
    rating_period_end DATE NOT NULL CHECK (rating_period_end >= rating_period_start),
    assessment_reasoning_quality INT NOT NULL CHECK (assessment_reasoning_quality BETWEEN 1 AND 5),
    therapy_effectiveness_score INT NOT NULL CHECK (therapy_effectiveness_score BETWEEN 1 AND 5)
);
```

## 12. Reports & Notifications
```sql
CREATE TABLE generated_report (
    generated_report_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES clinical_case(case_id),
    report_type report_type NOT NULL,
    report_content JSONB NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE TABLE notification (
    notification_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    recipient_id UUID NOT NULL REFERENCES "users"(user_id),
    notification_type notification_type NOT NULL,
    status notification_status NOT NULL DEFAULT 'UNREAD',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);
```

## 13. Consent
```sql
CREATE TABLE consent (
    consent_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patient(patient_id),
    consent_version INT DEFAULT 1 NOT NULL,
    consented_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    withdrawn_at TIMESTAMP WITH TIME ZONE,
    consent_data_collection BOOLEAN NOT NULL,
    consent_audio_video BOOLEAN NOT NULL,
    consent_ai_analysis BOOLEAN NOT NULL,
    consent_ai_training consent_ai_training NOT NULL,
    consent_signature_url TEXT NOT NULL,

    CONSTRAINT uq_patient_consent_version UNIQUE(patient_id, consent_version)
);
```

## 14. AI Artifacts
```sql
CREATE TABLE ai_artifact (
    ai_artifact_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    source_speech_sample_id UUID REFERENCES speech_sample(sample_id),
    source_video_observation_id UUID REFERENCES video_observation(video_observation_id),
    source_clinical_interpretation_id UUID REFERENCES clinical_interpretation(clinical_interpretation_id),
    model_version TEXT NOT NULL,
    analysis_type ai_analysis_type NOT NULL,
    ai_generated_result JSONB NOT NULL,
    confidence_score DECIMAL NOT NULL CHECK (confidence_score >= 0 AND confidence_score <= 1),
    human_review_status human_review_status NOT NULL,
    reviewed_by UUID REFERENCES "users"(user_id),
    reviewed_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_exactly_one_source CHECK (
        (source_speech_sample_id IS NOT NULL)::int +
        (source_video_observation_id IS NOT NULL)::int +
        (source_clinical_interpretation_id IS NOT NULL)::int = 1
    )
);
```

## 15. Audit Log
```sql
CREATE TABLE audit_log (
    audit_log_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    table_name TEXT NOT NULL,
    record_id UUID NOT NULL,
    action_performed audit_action NOT NULL,
    changed_fields_snapshot JSONB NOT NULL,
    actor_id UUID NOT NULL REFERENCES "users"(user_id),
    occurred_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);
```

## 16. Indexes
```sql
-- Identity & Access
CREATE INDEX idx_patient_name ON patient(legal_name);
CREATE INDEX idx_case_patient ON clinical_case(patient_id);
CREATE INDEX idx_case_therapist ON clinical_case(assigned_therapist_id);

-- Encounters & History
CREATE INDEX idx_encounter_case ON encounter(case_id);
CREATE INDEX idx_history_case ON medical_history(case_id);

-- Assessment Lookups
CREATE INDEX idx_assignment_encounter ON assessment_task_assignment(encounter_id);
CREATE INDEX idx_speech_sample_assignment ON speech_sample(assignment_id);
CREATE INDEX idx_articulation_encounter ON articulation_production_attempt(encounter_id);

-- Therapy & Supervision
CREATE INDEX idx_therapy_plan_case ON therapy_plan(case_id);
CREATE INDEX idx_therapy_session_encounter ON therapy_session(encounter_id);
CREATE INDEX idx_home_practice_session ON home_practice(session_id);
CREATE INDEX idx_supervisor_review_interpretation ON supervisor_review(reviewed_clinical_interpretation_id);
CREATE INDEX idx_supervisor_review_plan ON supervisor_review(reviewed_therapy_plan_id);

-- System
CREATE INDEX idx_audit_record ON audit_log(table_name, record_id);
CREATE INDEX idx_ai_artifact_sample ON ai_artifact(source_speech_sample_id);
```

## 17. CHECK constraints
*All CHECK constraints (e.g. mutually exclusive polymorphic foreign keys, scores constrained 1-5, and start/end dates constraints) have been securely defined directly on the `CREATE TABLE` scripts in the sections above.*

## 18. Seed/Reference data
```sql
INSERT INTO language_reference (language_code, language_name) VALUES
('EN', 'English'),
('HI', 'Hindi'),
('GU', 'Gujarati');

INSERT INTO clinical_problem_reference (problem_code, problem_name) VALUES
('ARTIC', 'Articulation Difficulty'),
('FLU', 'Fluency Concern'),
('VOC', 'Voice Concern'),
('LANG', 'Language Delay'),
('HEAR', 'Hearing Related Concern');
```

## 19. Transaction-safe migration order
*The order constructed in this document respects foreign key hierarchy for dependency-free drops and creations. For migrations, execute drops in exact reverse order to this file.*
