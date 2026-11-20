-- Migration V3: Case-insensitive functional index for fast authentication email lookups
-- Speeds up findByEmailIgnoreCase and existsByEmailIgnoreCase in Postgres
CREATE INDEX IF NOT EXISTS idx_users_email_lower ON users(LOWER(email));
