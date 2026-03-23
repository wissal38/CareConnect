-- Add is_active column to user and user_authenticated tables (if missing)
ALTER TABLE user ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE user_authenticated ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
