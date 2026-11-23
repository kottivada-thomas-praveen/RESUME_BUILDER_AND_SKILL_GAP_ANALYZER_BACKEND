-- Migration V4: User Analytics and Admin Monitoring System Schema
-- Adds visitor tracking, authentication audit logging, user activity tracking, and user login tracking

-- 1. Alter Users Table to track login frequency and last access
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS login_count INT DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_login_count INT DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_users_last_login ON users(last_login_at);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- 2. Visitor Logs Table (Website traffic, unique visitors, devices, page views)
CREATE TABLE IF NOT EXISTS visitor_logs (
    id BIGSERIAL PRIMARY KEY,
    visitor_id VARCHAR(64) NOT NULL,
    session_id VARCHAR(64),
    page_url VARCHAR(255) NOT NULL,
    page_title VARCHAR(150),
    referrer_url VARCHAR(255),
    ip_hash VARCHAR(64),
    user_agent VARCHAR(500),
    device_type VARCHAR(50),
    browser VARCHAR(50),
    os VARCHAR(50),
    country VARCHAR(100),
    city VARCHAR(100),
    duration_seconds INT DEFAULT 0,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_visitor_logs_created_at ON visitor_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_visitor_logs_visitor_id ON visitor_logs(visitor_id);
CREATE INDEX IF NOT EXISTS idx_visitor_logs_page_url ON visitor_logs(page_url);
CREATE INDEX IF NOT EXISTS idx_visitor_logs_user_id ON visitor_logs(user_id);

-- 3. Authentication Audit Logs (Successful, failed logins, logout, password resets)
CREATE TABLE IF NOT EXISTS auth_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    email VARCHAR(150),
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(255),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auth_logs_created_at ON auth_audit_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_auth_logs_email ON auth_audit_logs(email);
CREATE INDEX IF NOT EXISTS idx_auth_logs_event_type ON auth_audit_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_auth_logs_status ON auth_audit_logs(status);

-- 4. User Activity Logs (Resume creation, updates, downloads, uploads, ATS & skill analyses)
CREATE TABLE IF NOT EXISTS user_activity_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    user_email VARCHAR(150),
    activity_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_activity_logs_created_at ON user_activity_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_activity_logs_user_id ON user_activity_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_activity_logs_activity_type ON user_activity_logs(activity_type);
