-- Fix user password hashes
-- Version: 4.0
-- Description: Update password hashes for sample users to correct BCrypt hash for 'password123'

-- Set schema
SET search_path TO singtel_app;

-- Update all sample users with correct password hash for 'password123'
UPDATE users SET password_hash = '$2a$10$FthcQPUtqPvKREhD8hP7jO7HPFAybHRpu2ZoNLMzZ9A2HmmO.H2Qe' 
WHERE id IN (
    '880e8400-e29b-41d4-a716-446655440001',  -- john.doe
    '880e8400-e29b-41d4-a716-446655440002',  -- jane.smith
    '880e8400-e29b-41d4-a716-446655440003',  -- mike.wilson
    '880e8400-e29b-41d4-a716-446655440004',  -- sarah.lee
    '880e8400-e29b-41d4-a716-446655440005'   -- david.tan
);
