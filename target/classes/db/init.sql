-- =============================================================
-- SwineCore — MySQL Schema Bootstrap
-- Hibernate auto-creates tables via spring.jpa.hibernate.ddl-auto=update
-- This script provides optional indexes and initial verification.
-- Run ONLY once on a fresh database if needed.
-- =============================================================

-- The default admin account is seeded by DataInitializerService on startup:
-- Email:    admin@swinecore.com
-- Password: Admin@1234
-- Change this immediately after first login.

-- Optional: create the database if running manually
CREATE DATABASE IF NOT EXISTS swinecore
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE swinecore;

-- All table creation is handled by Hibernate (ddl-auto=update).
-- Hibernate will auto-migrate on startup.
-- Additional performance indexes (supplement Hibernate defaults):
-- (Run after first startup once tables exist)

-- CREATE INDEX IF NOT EXISTS idx_pig_birth_date    ON pigs(birth_date);
-- CREATE INDEX IF NOT EXISTS idx_task_date         ON daily_tasks(task_date);
-- CREATE INDEX IF NOT EXISTS idx_att_work_date     ON attendance(work_date);
-- CREATE INDEX IF NOT EXISTS idx_order_created     ON pig_orders(created_at);
-- CREATE INDEX IF NOT EXISTS idx_fin_created       ON finance_transactions(created_at);
