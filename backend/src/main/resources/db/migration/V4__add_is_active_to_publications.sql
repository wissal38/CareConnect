-- Add is_active column to publications or publication tables if missing
ALTER TABLE publications ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE publication ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
