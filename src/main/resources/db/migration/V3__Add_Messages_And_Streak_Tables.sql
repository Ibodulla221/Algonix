-- Messages table yaratish
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    coins_earned INTEGER,
    xp_earned INTEGER,
    new_level INTEGER,
    problem_title VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Messages table uchun indexlar
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_user_id') THEN
        CREATE INDEX idx_messages_user_id ON messages(user_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_created_at') THEN
        CREATE INDEX idx_messages_created_at ON messages(created_at);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_is_read') THEN
        CREATE INDEX idx_messages_is_read ON messages(is_read);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_type') THEN
        CREATE INDEX idx_messages_type ON messages(type);
    END IF;
END
$$;

-- User statistics table-ga streak fieldlarini qo'shish
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user_statistics' AND column_name = 'last_login_date') THEN
        ALTER TABLE user_statistics 
        ADD COLUMN last_login_date DATE,
        ADD COLUMN current_streak INTEGER DEFAULT 0,
        ADD COLUMN longest_streak INTEGER DEFAULT 0,
        ADD COLUMN weekly_streak INTEGER DEFAULT 0,
        ADD COLUMN monthly_streak INTEGER DEFAULT 0,
        ADD COLUMN last_weekly_reward DATE,
        ADD COLUMN last_monthly_reward DATE;
    END IF;
END
$$;