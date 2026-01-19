-- V5: Add contest-specific fields to problems table

-- Add contest-specific columns to problems table
DO $$
BEGIN
    -- Add is_contest_only column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'problems' AND column_name = 'is_contest_only') THEN
        ALTER TABLE problems ADD COLUMN is_contest_only BOOLEAN DEFAULT FALSE;
    END IF;
    
    -- Add contest_id column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'problems' AND column_name = 'contest_id') THEN
        ALTER TABLE problems ADD COLUMN contest_id BIGINT;
    END IF;
    
    -- Add publish_time column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'problems' AND column_name = 'publish_time') THEN
        ALTER TABLE problems ADD COLUMN publish_time TIMESTAMP;
    END IF;
END $$;

-- Add foreign key constraint for contest_id if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                   WHERE constraint_name = 'fk_problems_contest_id') THEN
        ALTER TABLE problems 
        ADD CONSTRAINT fk_problems_contest_id 
        FOREIGN KEY (contest_id) REFERENCES contests(id) ON DELETE SET NULL;
    END IF;
END $$;

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_problems_contest_only ON problems(is_contest_only);
CREATE INDEX IF NOT EXISTS idx_problems_contest_id ON problems(contest_id);
CREATE INDEX IF NOT EXISTS idx_problems_publish_time ON problems(publish_time);