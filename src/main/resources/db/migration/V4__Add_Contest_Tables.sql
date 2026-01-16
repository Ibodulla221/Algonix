-- Contest tables migration

CREATE TABLE contests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    start_time TIMESTAMP NOT NULL,
    duration_seconds INT NOT NULL,
    problem_count INT NOT NULL DEFAULT 0,
    participants_count INT NOT NULL DEFAULT 0,
    prize_pool TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING',
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
);

CREATE TABLE contest_problems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contest_id BIGINT NOT NULL,
    problem_id BIGINT NOT NULL,
    symbol VARCHAR(5) NOT NULL,
    points INT NOT NULL,
    order_index INT NOT NULL,
    attempts_count INT NOT NULL DEFAULT 0,
    solved_count INT NOT NULL DEFAULT 0,
    unsolved_count INT NOT NULL DEFAULT 0,
    attempt_users_count INT NOT NULL DEFAULT 0,
    delta DOUBLE,
    FOREIGN KEY (contest_id) REFERENCES contests(id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    UNIQUE KEY unique_contest_problem (contest_id, problem_id),
    INDEX idx_contest_order (contest_id, order_index)
);

CREATE TABLE contest_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    UNIQUE KEY unique_contest_user (contest_id, user_id),
    INDEX idx_contest_standings (contest_id, score DESC, total_penalty ASC),
    INDEX idx_user_history (user_id, registered_at DESC)
);

CREATE TABLE contest_submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
    INDEX idx_contest_user (contest_id, user_id, submitted_at DESC),
    INDEX idx_contest_problem (contest_id, contest_problem_id),
    INDEX idx_user_problem (contest_id, user_id, contest_problem_id)
);
