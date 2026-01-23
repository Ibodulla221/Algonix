-- Code Templates table
CREATE TABLE code_templates (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    code TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(problem_id, language)
);

-- Index for faster queries
CREATE INDEX idx_code_templates_problem_language ON code_templates(problem_id, language);
CREATE INDEX idx_code_templates_language ON code_templates(language);

-- Comments
COMMENT ON TABLE code_templates IS 'Code templates for problems in different programming languages';
COMMENT ON COLUMN code_templates.problem_id IS 'Reference to the problem';
COMMENT ON COLUMN code_templates.language IS 'Programming language (java, python, javascript, cpp, etc.)';
COMMENT ON COLUMN code_templates.code IS 'Template code for the language';