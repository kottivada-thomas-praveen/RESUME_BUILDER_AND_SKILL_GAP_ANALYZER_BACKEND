-- Performance Optimization Migration
-- Adds composite indexes missing from V1 (identified during performance analysis, Nov 2026).

-- Composite index on resumes for the most common query pattern:
--   ResumeRepository.findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc()
-- Without this, every dashboard load causes a full sequential scan of the resumes table filtered by user_id.
CREATE INDEX IF NOT EXISTS idx_resumes_user_active_updated ON resumes(user_id, is_active, updated_at DESC);

-- Index on ats_analyses for per-user listing ordered by creation date.
-- Without this, every AnalyzeResume page load scans all analyses rows.
CREATE INDEX IF NOT EXISTS idx_ats_analyses_user_created ON ats_analyses(user_id, created_at DESC);

-- Index on user_profiles for the most common lookup: findByUserId().
CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON user_profiles(user_id);
