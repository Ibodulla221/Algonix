-- Migration fayli - faqat mavjud bo'lmagan table'lar va column'larni qo'shish

-- Agar mavjud table'lar yo'q bo'lsa, ularni yaratish
-- Lekin ko'p table'lar allaqachon mavjud, shuning uchun faqat kerakli column'larni qo'shamiz

-- User statistics table'ga kerakli column'larni qo'shish
DO $$
BEGIN
    -- Agar user_statistics table'da coins column'i yo'q bo'lsa, qo'shamiz
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user_statistics' AND column_name = 'coins') THEN
        ALTER TABLE user_statistics ADD COLUMN coins INTEGER DEFAULT 0;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user_statistics' AND column_name = 'xp') THEN
        ALTER TABLE user_statistics ADD COLUMN xp INTEGER DEFAULT 0;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user_statistics' AND column_name = 'level') THEN
        ALTER TABLE user_statistics ADD COLUMN level INTEGER DEFAULT 1;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'user_statistics' AND column_name = 'ranking') THEN
        ALTER TABLE user_statistics ADD COLUMN ranking INTEGER DEFAULT 0;
    END IF;
END
$$;

-- Kerakli indexlar yaratish
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_users_username') THEN
        CREATE INDEX idx_users_username ON users(username);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_users_email') THEN
        CREATE INDEX idx_users_email ON users(email);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_problems_difficulty') THEN
        CREATE INDEX idx_problems_difficulty ON problems(difficulty);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_submissions_user_id') THEN
        CREATE INDEX idx_submissions_user_id ON submissions(user_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_submissions_problem_id') THEN
        CREATE INDEX idx_submissions_problem_id ON submissions(problem_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_submissions_status') THEN
        CREATE INDEX idx_submissions_status ON submissions(status);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_statistics_user_id') THEN
        CREATE INDEX idx_user_statistics_user_id ON user_statistics(user_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_favourites_user_id') THEN
        CREATE INDEX idx_favourites_user_id ON favourites(user_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_favourites_problem_id') THEN
        CREATE INDEX idx_favourites_problem_id ON favourites(problem_id);
    END IF;
END
$$;