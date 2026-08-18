CREATE TYPE IF NOT EXISTS user_role AS ENUM ('PATIENT', 'PARENT_GUARDIAN', 'THERAPIST', 'SUPERVISOR', 'DOCTOR', 'ADMIN', 'SYSTEM');
CREATE TYPE IF NOT EXISTS user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED');
CREATE TYPE IF NOT EXISTS case_status AS ENUM ('ACTIVE', 'DISCHARGED', 'ON_HOLD');
CREATE TYPE IF NOT EXISTS encounter_type AS ENUM ('INITIAL_EVALUATION', 'THERAPY_SESSION', 'FOLLOW_UP', 'REASSESSMENT');
CREATE TYPE IF NOT EXISTS assessment_reason AS ENUM ('CHIEF_COMPLAINT', 'SCREENING', 'INITIAL_ASSESSMENT', 'REASSESSMENT', 'FOLLOW_UP', 'DOCTOR_REFERRAL', 'THERAPY_PROGRESS_EVALUATION', 'THERAPIST_CONCERN', 'SUPERVISOR_REQUEST');
CREATE TYPE IF NOT EXISTS sex_at_birth AS ENUM ('MALE', 'FEMALE', 'INTERSEX', 'UNDISCLOSED');
CREATE TYPE IF NOT EXISTS document_classification AS ENUM ('MEDICAL_REPORT', 'AUDIO_REPORT', 'AUDIOLOGY_REPORT', 'CONSENT_FORM', 'CLINICAL_NOTES', 'OTHER');
CREATE TYPE IF NOT EXISTS delivery_type AS ENUM ('VAGINAL', 'CESAREAN', 'INSTRUMENTAL', 'UNKNOWN');
CREATE TYPE IF NOT EXISTS prematurity_status AS ENUM ('FULL_TERM', 'PRETERM', 'UNKNOWN');
CREATE TYPE IF NOT EXISTS motor_milestones_status AS ENUM ('TYPICAL', 'DELAYED', 'UNKNOWN');
CREATE TYPE IF NOT EXISTS task_type AS ENUM ('SPONTANEOUS', 'READING', 'PICTURE_DESCRIPTION', 'TARGET_WORD');
CREATE TYPE IF NOT EXISTS target_type AS ENUM ('ISOLATED_SOUND', 'SYLLABLE', 'WORD', 'PHRASE', 'SENTENCE', 'CONNECTED_SPEECH');
CREATE TYPE IF NOT EXISTS target_position AS ENUM ('INITIAL', 'MEDIAL', 'FINAL');
CREATE TYPE IF NOT EXISTS assignment_status AS ENUM ('PENDING', 'ATTEMPTED', 'COMPLETED', 'SKIPPED');
CREATE TYPE IF NOT EXISTS speaker_type AS ENUM ('PATIENT', 'PARENT', 'THERAPIST', 'MULTIPLE');
CREATE TYPE IF NOT EXISTS recording_quality AS ENUM ('GOOD', 'ACCEPTABLE', 'POOR', 'UNUSABLE');
CREATE TYPE IF NOT EXISTS noise_level AS ENUM ('NONE', 'LOW', 'HIGH');
CREATE TYPE IF NOT EXISTS analysis_eligibility AS ENUM ('ELIGIBLE', 'NOT_ELIGIBLE', 'PENDING_REVIEW');
CREATE TYPE IF NOT EXISTS analysis_ineligibility_reason AS ENUM ('NOISE', 'INCOMPLETE', 'MULTIPLE_SPEAKERS', 'WRONG_LANGUAGE', 'INSUFFICIENT_DURATION', 'POOR_RECORDING', 'UNSUPPORTED_FORMAT', 'UNKNOWN');
CREATE TYPE IF NOT EXISTS screening_environment AS ENUM ('QUIET_ROOM', 'SOUND_TREATED_ROOM', 'OPEN_CLINIC');
CREATE TYPE IF NOT EXISTS screening_outcome AS ENUM ('NO_CONCERN', 'POSSIBLE_CONCERN', 'INCONCLUSIVE', 'UNABLE_TO_COMPLETE');
CREATE TYPE IF NOT EXISTS clinical_recommended_action AS ENUM ('THERAPY', 'FURTHER_ASSESSMENT', 'DOCTOR_REFERRAL', 'CONTINUE_MONITORING', 'INSUFFICIENT_EVIDENCE');
CREATE TYPE IF NOT EXISTS hearing_recommended_action AS ENUM ('NO_IMMEDIATE_ACTION', 'REPEAT_SCREENING', 'DIAGNOSTIC_HEARING_EVALUATION', 'CLINICIAN_REVIEW');
CREATE TYPE IF NOT EXISTS ear_type AS ENUM ('LEFT', 'RIGHT', 'BILATERAL');
CREATE TYPE IF NOT EXISTS response_method AS ENUM ('BUTTON_PRESS', 'HAND_RAISE', 'VERBAL_RESPONSE', 'CLINICIAN_OBSERVATION', 'OTHER');
CREATE TYPE IF NOT EXISTS patient_response AS ENUM ('CONSISTENT', 'INCONSISTENT', 'NO_RESPONSE');
CREATE TYPE IF NOT EXISTS articulation_accuracy AS ENUM ('CORRECT', 'SUBSTITUTION', 'OMISSION', 'DISTORTION', 'ADDITION');
CREATE TYPE IF NOT EXISTS speech_rate_observation AS ENUM ('TYPICAL', 'FAST', 'SLOW');
CREATE TYPE IF NOT EXISTS speech_rate_unit AS ENUM ('SYLLABLES_PER_MIN', 'WORDS_PER_MIN');
CREATE TYPE IF NOT EXISTS pitch_observation AS ENUM ('TYPICAL', 'TOO_HIGH', 'TOO_LOW');
CREATE TYPE IF NOT EXISTS loudness_observation AS ENUM ('TYPICAL', 'TOO_LOUD', 'TOO_SOFT');
CREATE TYPE IF NOT EXISTS resonance_observation AS ENUM ('TYPICAL', 'HYPERNASAL', 'HYPONASAL', 'MIXED');
CREATE TYPE IF NOT EXISTS voice_quality AS ENUM ('BREATHY', 'HARSH', 'HOARSE', 'NASAL', 'TYPICAL');
CREATE TYPE IF NOT EXISTS measurement_source AS ENUM ('CLINICIAN', 'DEVICE', 'SOFTWARE', 'AI');
CREATE TYPE IF NOT EXISTS camera_position AS ENUM ('FRONTAL', 'PROFILE', 'OFF_ANGLE');
CREATE TYPE IF NOT EXISTS clinical_assessment_status AS ENUM ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'FINALIZED');
CREATE TYPE IF NOT EXISTS goal_type AS ENUM ('LONG_TERM', 'SHORT_TERM');
CREATE TYPE IF NOT EXISTS therapy_domain AS ENUM ('ARTICULATION', 'FLUENCY', 'VOICE', 'LANGUAGE', 'HEARING');
CREATE TYPE IF NOT EXISTS metric_type AS ENUM ('BASELINE', 'TARGET', 'PROGRESS_CHECK');
CREATE TYPE IF NOT EXISTS frequency_unit AS ENUM ('WEEK', 'MONTH');
CREATE TYPE IF NOT EXISTS supervisor_approval_status AS ENUM ('PENDING', 'APPROVED', 'MODIFY', 'REJECTED');
CREATE TYPE IF NOT EXISTS session_status AS ENUM ('COMPLETED', 'CANCELLED', 'MISSED', 'RESCHEDULED');
CREATE TYPE IF NOT EXISTS completion_status AS ENUM ('NOT_STARTED', 'PARTIALLY_COMPLETED', 'COMPLETED');
CREATE TYPE IF NOT EXISTS progress_status AS ENUM ('IMPROVED', 'STABLE', 'DECLINED', 'ACHIEVED', 'INSUFFICIENT_DATA');
CREATE TYPE IF NOT EXISTS supervisor_review_status AS ENUM ('PENDING', 'COMPLETED');
CREATE TYPE IF NOT EXISTS supervisor_action AS ENUM ('APPROVE', 'RETURN_FOR_CORRECTION', 'MODIFY');
CREATE TYPE IF NOT EXISTS report_type AS ENUM ('BASELINE', 'MONTHLY_PROGRESS', 'DISCHARGE_SUMMARY');
CREATE TYPE IF NOT EXISTS notification_type AS ENUM ('PENDING_REVIEW', 'CORRECTION_REQUIRED');
CREATE TYPE IF NOT EXISTS notification_status AS ENUM ('UNREAD', 'READ', 'DISMISSED');
CREATE TYPE IF NOT EXISTS ai_analysis_type AS ENUM ('SPEECH_FEATURE_EXTRACTION', 'SUMMARIZATION');
CREATE TYPE IF NOT EXISTS human_review_status AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'MODIFIED');
CREATE TYPE IF NOT EXISTS consent_ai_training AS ENUM ('GRANTED', 'DENIED', 'NOT_ASKED', 'WITHDRAWN');
CREATE TYPE IF NOT EXISTS audit_action AS ENUM ('CREATE', 'UPDATE', 'SOFT_DELETE');
CREATE TYPE IF NOT EXISTS verification_status AS ENUM ('UNVERIFIED', 'VERIFIED');
CREATE TYPE IF NOT EXISTS interpretation_status AS ENUM ('NOT_INTERPRETED', 'CLINICIAN_INTERPRETED');
CREATE TYPE IF NOT EXISTS data_classification AS ENUM ('RAW', 'OBSERVATION', 'INTERPRETATION', 'AI_GENERATED');
CREATE TYPE IF NOT EXISTS data_provenance AS ENUM ('PATIENT', 'PARENT', 'THERAPIST', 'SUPERVISOR', 'DOCTOR', 'SYSTEM');
CREATE TYPE IF NOT EXISTS clinical_discussion_status AS ENUM ('OPEN', 'RESOLVED', 'CLOSED');
CREATE TYPE IF NOT EXISTS clinical_discussion_priority AS ENUM ('LOW', 'ROUTINE', 'URGENT');
CREATE TYPE IF NOT EXISTS correction_status AS ENUM ('OPEN', 'RESOLVED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS voice_assessment_voice_quality (
    voice_assessment_id UUID NOT NULL,
    voice_quality VARCHAR(50) NOT NULL,
    PRIMARY KEY (voice_assessment_id, voice_quality)
);

CREATE TABLE IF NOT EXISTS clinical_interpretation_history (
    history_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    clinical_interpretation_id UUID NOT NULL,
    version_number INT NOT NULL,
    evidence_summary TEXT NOT NULL,
    clinical_assessment_status clinical_assessment_status NOT NULL,
    recommended_action clinical_recommended_action NOT NULL,
    snapshot_created_by UUID,
    snapshot_created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_clinical_interpretation_history UNIQUE (clinical_interpretation_id, version_number)
);

CREATE TABLE IF NOT EXISTS clinical_interpretation_problem_history (
    history_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    clinical_interpretation_id UUID NOT NULL,
    version_number INT NOT NULL,
    problem_code VARCHAR(50) NOT NULL,
    snapshot_created_by UUID,
    snapshot_created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_clinical_interpretation_problem_history UNIQUE (clinical_interpretation_id, version_number, problem_code)
);

CREATE TABLE IF NOT EXISTS correction_request (
    correction_request_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    clinical_interpretation_id UUID NOT NULL,
    supervisor_review_id UUID NOT NULL,
    requested_by UUID NOT NULL,
    requested_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    reason TEXT NOT NULL,
    status correction_status NOT NULL DEFAULT 'OPEN',
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by UUID
);

CREATE TABLE IF NOT EXISTS supervisor_review_history (
    history_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    supervisor_review_id UUID NOT NULL,
    clinical_interpretation_id UUID,
    review_status supervisor_review_status NOT NULL,
    supervisor_action supervisor_action NOT NULL,
    supervisor_comments TEXT,
    version_number INT NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    recorded_by UUID NOT NULL,
    CONSTRAINT uq_supervisor_review_history UNIQUE (supervisor_review_id, version_number)
);

CREATE INDEX IF NOT EXISTS idx_clinical_interpretation_history_interpretation
    ON clinical_interpretation_history(clinical_interpretation_id, version_number);
CREATE INDEX IF NOT EXISTS idx_problem_history_interpretation
    ON clinical_interpretation_problem_history(clinical_interpretation_id, version_number);
CREATE INDEX IF NOT EXISTS idx_correction_request_interpretation
    ON correction_request(clinical_interpretation_id);
CREATE INDEX IF NOT EXISTS idx_supervisor_review_history_review
    ON supervisor_review_history(supervisor_review_id, version_number);