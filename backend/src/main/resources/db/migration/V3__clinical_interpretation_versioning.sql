CREATE TYPE correction_status AS ENUM ('OPEN', 'RESOLVED');

CREATE TABLE clinical_interpretation_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id),
    version_number INT NOT NULL,
    evidence_summary TEXT NOT NULL,
    clinical_assessment_status clinical_assessment_status NOT NULL,
    recommended_action clinical_recommended_action NOT NULL,
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE TABLE clinical_interpretation_problem_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id),
    problem_code VARCHAR(50) NOT NULL REFERENCES clinical_problem_reference(problem_code),
    version_number INT NOT NULL,
    created_by UUID REFERENCES "users"(user_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE TABLE correction_request (
    correction_request_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id),
    supervisor_review_id UUID NOT NULL REFERENCES supervisor_review(review_id),
    requested_by UUID NOT NULL REFERENCES "users"(user_id),
    requested_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    reason TEXT NOT NULL,
    status correction_status NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by UUID REFERENCES "users"(user_id)
);

CREATE TABLE supervisor_review_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    supervisor_review_id UUID NOT NULL REFERENCES supervisor_review(review_id),
    interpretation_id UUID NOT NULL REFERENCES clinical_interpretation(clinical_interpretation_id),
    action supervisor_action NOT NULL,
    review_comments TEXT,
    reviewer_id UUID NOT NULL REFERENCES "users"(user_id),
    reviewed_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    version_number INT NOT NULL
);
