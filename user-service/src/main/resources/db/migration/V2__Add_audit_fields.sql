-- Add audit fields to users and roles tables
ALTER TABLE users 
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE roles 
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN updated_by VARCHAR(255);

-- Update existing records to have 'system' as creator
UPDATE users SET created_by = 'system', updated_by = 'system' WHERE created_by IS NULL;
UPDATE roles SET created_by = 'system', updated_by = 'system' WHERE created_by IS NULL;
