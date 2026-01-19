-- Remove contestId column from problems table
-- Contest va Problem bog'lanishi endi ContestProblem orqali amalga oshiriladi

ALTER TABLE problems DROP COLUMN IF EXISTS contest_id;