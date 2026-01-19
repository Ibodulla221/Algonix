-- Flyway migration history'ni tozalash
DELETE FROM flyway_schema_history WHERE version >= '2';

-- Agar kerak bo'lsa, barcha migration'larni tozalash
-- DROP TABLE IF EXISTS flyway_schema_history;