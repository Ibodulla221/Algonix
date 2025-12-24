-- Migration script to update difficulty levels from EASY/MEDIUM/HARD to BEGINNER/BASIC/NORMAL/MEDIUM/HARD

-- Update existing problems
-- EASY -> BASIC (most EASY problems will become BASIC)
UPDATE problems SET difficulty = 'BASIC' WHERE difficulty = 'EASY';

-- MEDIUM -> NORMAL (most MEDIUM problems will become NORMAL) 
UPDATE problems SET difficulty = 'NORMAL' WHERE difficulty = 'MEDIUM';

-- HARD stays HARD
-- UPDATE problems SET difficulty = 'HARD' WHERE difficulty = 'HARD'; -- No change needed

-- Note: We can manually adjust specific problems later if needed
-- For example, some BASIC problems might need to be BEGINNER
-- And some NORMAL problems might need to be MEDIUM

-- Show updated counts
SELECT difficulty, COUNT(*) as count FROM problems GROUP BY difficulty ORDER BY difficulty;