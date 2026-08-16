-- V3 depends on V1__initial_schema.sql.
-- V1 owns supervisor_review and supervisor_action; they are intentionally not recreated here.

CREATE TYPE correction_status AS ENUM ('OPEN', 'RESOLVED', 'CANCELLED');

CREATE TABLE clinical_interpretation_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    clinical_interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id),
    version_number INT NOT NULL,
    evidence_summary TEXT NOT NULL,
    clinical_assessment_status clinical_assessment_status NOT NULL,
    recommended_action clinical_recommended_action NOT NULL,
    snapshot_created_by UUID REFERENCES "users"(user_id),
    snapshot_created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_clinical_interpretation_history UNIQUE (clinical_interpretation_id, version_number)
);

CREATE TABLE clinical_interpretation_problem_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    clinical_interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id),
    version_number INT NOT NULL,
    problem_code VARCHAR(50) NOT NULL REFERENCES clinical_problem_reference(problem_code),
    snapshot_created_by UUID REFERENCES "users"(user_id),
    snapshot_created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_clinical_interpretation_problem_history UNIQUE (clinical_interpretation_id, version_number, problem_code)
);

CREATE TABLE correction_request (
    correction_request_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    clinical_interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id),
    supervisor_review_id UUID NOT NULL REFERENCES supervisor_review(review_id),
    requested_by UUID NOT NULL REFERENCES "users"(user_id),
    requested_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    reason TEXT NOT NULL,
    status correction_status NOT NULL DEFAULT 'OPEN',
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by UUID REFERENCES "users"(user_id)
);

CREATE TABLE supervisor_review_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    supervisor_review_id UUID NOT NULL REFERENCES supervisor_review(review_id),
    clinical_interpretation_id UUID REFERENCES clinical_interpretation(clinical_interpretation_id),
    review_status supervisor_review_status NOT NULL,
    supervisor_action supervisor_action NOT NULL,
    supervisor_comments TEXT,
    version_number INT NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    recorded_by UUID NOT NULL REFERENCES "users"(user_id),
    CONSTRAINT uq_supervisor_review_history UNIQUE (supervisor_review_id, version_number)
);

CREATE INDEX idx_clinical_interpretation_history_interpretation
    ON clinical_interpretation_history(clinical_interpretation_id, version_number);
CREATE INDEX idx_problem_history_interpretation
    ON clinical_interpretation_problem_history(clinical_interpretation_id, version_number);
CREATE INDEX idx_correction_request_interpretation
    ON correction_request(clinical_interpretation_id);
CREATE INDEX idx_supervisor_review_history_review
    ON supervisor_review_history(supervisor_review_id, version_number);
