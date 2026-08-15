INSERT INTO language_reference (language_code, language_name) VALUES
('EN', 'English'),
('HI', 'Hindi'),
('GU', 'Gujarati')
ON CONFLICT (language_code) DO UPDATE
SET language_name = EXCLUDED.language_name;

INSERT INTO clinical_problem_reference (problem_code, problem_name) VALUES
('ARTIC', 'Articulation Difficulty'),
('FLU', 'Fluency Concern'),
('VOC', 'Voice Concern'),
('LANG', 'Language Delay'),
('HEAR', 'Hearing Related Concern')
ON CONFLICT (problem_code) DO UPDATE
SET problem_name = EXCLUDED.problem_name;
