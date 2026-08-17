-- H2 compatibility aliases for PostgreSQL enum/domain-backed fields.
-- Keep these permissive VARCHAR domains so Hibernate can create the JPA schema in tests.

CREATE DOMAIN IF NOT EXISTS user_role AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS user_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS case_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS encounter_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS assessment_reason AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS sex_at_birth AS VARCHAR;

CREATE DOMAIN IF NOT EXISTS task_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS target_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS target_position AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS assignment_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS speaker_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS recording_quality AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS noise_level AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS analysis_eligibility AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS analysis_ineligibility_reason AS VARCHAR;

CREATE DOMAIN IF NOT EXISTS screening_environment AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS screening_outcome AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS clinical_recommended_action AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS hearing_recommended_action AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS ear_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS response_method AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS patient_response AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS articulation_accuracy AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS speech_rate_observation AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS speech_rate_unit AS VARCHAR;

CREATE DOMAIN IF NOT EXISTS pitch_observation AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS loudness_observation AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS resonance_observation AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS voice_quality AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS measurement_source AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS camera_position AS VARCHAR;

CREATE DOMAIN IF NOT EXISTS clinical_assessment_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS goal_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS therapy_domain AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS metric_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS frequency_unit AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS supervisor_approval_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS session_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS completion_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS progress_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS supervisor_review_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS supervisor_action AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS report_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS notification_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS notification_status AS VARCHAR;

CREATE DOMAIN IF NOT EXISTS ai_analysis_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS human_review_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS consent_ai_training AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS audit_action AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS verification_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS interpretation_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS data_classification AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS data_provenance AS VARCHAR;

CREATE DOMAIN IF NOT EXISTS correction_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS delivery_type AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS prematurity_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS motor_milestones_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS clinical_discussion_priority AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS clinical_discussion_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS doctor_recommendation_status AS VARCHAR;
CREATE DOMAIN IF NOT EXISTS doctor_recommendation_type AS VARCHAR;

-- Test-only support for the clinical interpretation version-history writes.
-- These tables mirror the columns used by ClinicalInterpretationService.snapshot().
-- They intentionally omit production-only foreign keys because schema-h2.sql runs
-- before Hibernate creates the JPA tables under ddl-auto=create-drop.
CREATE TABLE IF NOT EXISTS clinical_interpretation_history (
    clinical_interpretation_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    evidence_summary VARCHAR(10000) NOT NULL,
    clinical_assessment_status clinical_assessment_status NOT NULL,
    recommended_action clinical_recommended_action NOT NULL,
    PRIMARY KEY (clinical_interpretation_id, version_number)
);

CREATE TABLE IF NOT EXISTS clinical_interpretation_problem_history (
    clinical_interpretation_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    problem_code VARCHAR(50) NOT NULL,
    PRIMARY KEY (clinical_interpretation_id, version_number, problem_code)
);
