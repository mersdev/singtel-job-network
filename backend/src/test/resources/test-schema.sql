-- Test schema initialization
-- Enable pgcrypto extension for gen_random_uuid() function
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create the singtel_app schema for tests
CREATE SCHEMA IF NOT EXISTS singtel_app;

-- Set the search path to include the singtel_app schema
SET search_path TO singtel_app, public;
