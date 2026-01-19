-- Create all necessary tables if they don't exist

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    bio TEXT,
    location VARCHAR(255),
    company VARCHAR(255),
    job_title VARCHAR(255),
    website VARCHAR(255),
    github_username VARCHAR(255),
    linkedin_url VARCHAR(255),
    twitter_username VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create problems table
CREATE TABLE IF NOT EXISTS problems (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    description TEXT,
    description_html TEXT,
    constraints TEXT[],
    hints TEXT[],
    categories TEXT[],
    tags TEXT[],
    code_templates JSONB,
    related_problem_id BIGINT[],
    companies TEXT[],
    likes INTEGER DEFAULT 0,
    dislikes INTEGER DEFAULT 0,
    acceptance_rate DOUBLE PRECISION DEFAULT 0.0,
    total_submissions BIGINT DEFAULT 0,
    total_accepted BIGINT DEFAULT 0,
    frequency DOUBLE PRECISION DEFAULT 0.0,
    is_premium BOOLEAN DEFAULT FALSE,
    time_limit_ms INTEGER DEFAULT 2000,
    memory_limit_mb INTEGER DEFAULT 512,
    global_sequence_number INTEGER,
    is_contest_only BOOLEAN DEFAULT FALSE,
    publish_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create problem_examples table
CREATE TABLE IF NOT EXISTS problem_examples (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT REFERENCES problems(id) ON DELETE CASCADE,
    case_number VARCHAR(10),
    input TEXT,
    target VARCHAR(255),
    output TEXT,
    explanation TEXT
);

-- Create test_cases table
CREATE TABLE IF NOT EXISTS test_cases (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT REFERENCES problems(id) ON DELETE CASCADE,
    input TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    is_hidden BOOLEAN DEFAULT FALSE,
    time_limit_ms INTEGER DEFAULT 2000
);

-- Create submissions table
CREATE TABLE IF NOT EXISTS submissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    problem_id BIGINT REFERENCES problems(id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    code TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    runtime INTEGER,
    memory_usage INTEGER,
    error_message TEXT,
    test_results JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_statistics table
CREATE TABLE IF NOT EXISTS user_statistics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    total_problems_solved INTEGER DEFAULT 0,
    easy_problems_solved INTEGER DEFAULT 0,
    medium_problems_solved INTEGER DEFAULT 0,
    hard_problems_solved INTEGER DEFAULT 0,
    total_submissions INTEGER DEFAULT 0,
    acceptance_rate DOUBLE PRECISION DEFAULT 0.0,
    ranking INTEGER DEFAULT 0,
    coins INTEGER DEFAULT 0,
    xp INTEGER DEFAULT 0,
    level INTEGER DEFAULT 1,
    current_streak INTEGER DEFAULT 0,
    max_streak INTEGER DEFAULT 0,
    weekly_streak INTEGER DEFAULT 0,
    last_solved_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create favourites table
CREATE TABLE IF NOT EXISTS favourites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    problem_id BIGINT REFERENCES problems(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, problem_id)
);

-- Create sequences if they don't exist
CREATE SEQUENCE IF NOT EXISTS users_seq START 1;
CREATE SEQUENCE IF NOT EXISTS problems_seq START 1;

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_problems_difficulty ON problems(difficulty);
CREATE INDEX IF NOT EXISTS idx_problems_slug ON problems(slug);
CREATE INDEX IF NOT EXISTS idx_submissions_user_id ON submissions(user_id);
CREATE INDEX IF NOT EXISTS idx_submissions_problem_id ON submissions(problem_id);
CREATE INDEX IF NOT EXISTS idx_submissions_status ON submissions(status);
CREATE INDEX IF NOT EXISTS idx_user_statistics_user_id ON user_statistics(user_id);
CREATE INDEX IF NOT EXISTS idx_favourites_user_id ON favourites(user_id);
CREATE INDEX IF NOT EXISTS idx_favourites_problem_id ON favourites(problem_id);