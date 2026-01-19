-- Contest tables migration

CREATE TABLE IF NOT EXISTS contests (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    start_time TIMESTAMP NOT NULL,
    duration_seconds INT NOT NULL,
    problem_count INT NOT NULL DEFAULT 0,
    participants_count INT NOT NULL DEFAULT 0,
    prize_pool TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING'
);

CREATE TABLE IF NOT EXISTS contest_problems (
    id BIGSERIAL PRIMARY KEY,
    contest_id BIGINT NOT NULL,
    problem_id BIGINT NOT NULL,
    symbol VARCHAR(5) NOT NULL,
    points INT NOT NULL,
    order_index INT NOT NULL,
    attempts_count INT NOT NULL DEFAULT 0,
    solved_count INT NOT NULL DEFAULT 0,
    unsolved_count INT NOT NULL DEFAULT 0,
    attempt_users_count INT NOT NULL DEFAULT 0,
    delta DOUBLE PRECISION,
    FOREIGN KEY (contest_id) REFERENCES contests(id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    UNIQUE (contest_id, problem_id)
);

CREATE TABLE IF NOT EXISTS contest_participants (
    id BIGSERIAL PRIMARY KEY,
    contest_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    registered_at TIMESTAMP NOT NULL,
    score INT NOT NULL DEFAULT 0,
    rank_position INT NOT NULL DEFAULT 0,
    rating_change INT NOT NULL DEFAULT 0,
    problems_solved INT NOT NULL DEFAULT 0,
    total_penalty BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (contest_id) REFERENCES contests(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (contest_id, user_id)
);

CREATE TABLE IF NOT EXISTS contest_submissions (
    id BIGSERIAL PRIMARY KEY,
    contest_id BIGINT NOT NULL,
    contest_problem_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    submission_id BIGINT NOT NULL,
    submitted_at TIMESTAMP NOT NULL,
    is_accepted BOOLEAN NOT NULL,
    score INT,
    time_taken BIGINT,
    FOREIGN KEY (contest_id) REFERENCES contests(id) ON DELETE CASCADE,
    FOREIGN KEY (contest_problem_id) REFERENCES contest_problems(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
);

-- Contest indexes
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contests_status') THEN
        CREATE INDEX idx_contests_status ON contests(status);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contests_start_time') THEN
        CREATE INDEX idx_contests_start_time ON contests(start_time);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contest_problems_contest_order') THEN
        CREATE INDEX idx_contest_problems_contest_order ON contest_problems(contest_id, order_index);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contest_participants_standings') THEN
        CREATE INDEX idx_contest_participants_standings ON contest_participants(contest_id, score DESC, total_penalty ASC);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contest_participants_user_history') THEN
        CREATE INDEX idx_contest_participants_user_history ON contest_participants(user_id, registered_at DESC);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contest_submissions_contest_user') THEN
        CREATE INDEX idx_contest_submissions_contest_user ON contest_submissions(contest_id, user_id, submitted_at DESC);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contest_submissions_contest_problem') THEN
        CREATE INDEX idx_contest_submissions_contest_problem ON contest_submissions(contest_id, contest_problem_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_contest_submissions_user_problem') THEN
        CREATE INDEX idx_contest_submissions_user_problem ON contest_submissions(contest_id, user_id, contest_problem_id);
    END IF;
END
$$;
