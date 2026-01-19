-- Add missing columns to users table if they don't exist
DO $$
BEGIN
    -- Add first_name column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'first_name') THEN
        ALTER TABLE users ADD COLUMN first_name VARCHAR(255);
    END IF;
    
    -- Add last_name column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'last_name') THEN
        ALTER TABLE users ADD COLUMN last_name VARCHAR(255);
    END IF;
END
$$;

-- Admin user yaratish (agar mavjud bo'lmasa)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin') THEN
        INSERT INTO users (username, email, password, first_name, last_name, role) 
        VALUES ('admin', 'admin@algonix.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'System', 'Administrator', 'ADMIN');
    END IF;
END
$$;

-- Admin uchun user statistics yaratish
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM user_statistics us 
        JOIN users u ON us.user_id = u.id 
        WHERE u.username = 'admin'
    ) THEN
        INSERT INTO user_statistics (user_id, total_problems_solved, easy_problems_solved, medium_problems_solved, hard_problems_solved, total_submissions, acceptance_rate, ranking, coins, xp, level)
        SELECT u.id, 0, 0, 0, 0, 0, 0.0, 0, 1000, 0, 1
        FROM users u 
        WHERE u.username = 'admin';
    END IF;
END
$$;