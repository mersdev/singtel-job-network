-- Initial database schema for Singtel Network On-Demand platform
-- Version: 1.0
-- Description: Core tables for users, companies, services, and orders

-- Enable pgcrypto extension for gen_random_uuid() function
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS singtel_app;

-- Set schema
SET search_path TO singtel_app;

-- Companies table - Business entities that subscribe to services
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Singapore',
    industry VARCHAR(100),
    company_size VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'INACTIVE')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Users table - Individual users associated with companies
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER', 'VIEWER')),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'INACTIVE')),
    last_login_at TIMESTAMP WITH TIME ZONE,
    password_changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Service categories for organizing services
CREATE TABLE service_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Services table - Catalog of available network services
CREATE TABLE services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID REFERENCES service_categories(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    service_type VARCHAR(100) NOT NULL, -- 'FIBER', 'VPN', 'DEDICATED_LINE', etc.
    base_bandwidth_mbps INTEGER,
    max_bandwidth_mbps INTEGER,
    min_bandwidth_mbps INTEGER,
    base_price_monthly DECIMAL(10,2),
    price_per_mbps DECIMAL(10,4),
    setup_fee DECIMAL(10,2) DEFAULT 0,
    contract_term_months INTEGER DEFAULT 12,
    is_bandwidth_adjustable BOOLEAN DEFAULT false,
    is_available BOOLEAN DEFAULT true,
    provisioning_time_hours INTEGER DEFAULT 24,
    features JSONB, -- Store service features as JSON
    technical_specs JSONB, -- Store technical specifications
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Service instances - Active services subscribed by companies
CREATE TABLE service_instances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    service_id UUID NOT NULL REFERENCES services(id),
    instance_name VARCHAR(255) NOT NULL,
    current_bandwidth_mbps INTEGER NOT NULL,
    installation_address TEXT NOT NULL,
    postal_code VARCHAR(20),
    contact_person VARCHAR(255),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROVISIONING', 'ACTIVE', 'SUSPENDED', 'TERMINATED')),
    monthly_cost DECIMAL(10,2),
    contract_start_date DATE,
    contract_end_date DATE,
    last_bandwidth_change_at TIMESTAMP WITH TIME ZONE,
    provisioned_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Orders table - Track service provisioning orders
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    service_id UUID NOT NULL REFERENCES services(id),
    service_instance_id UUID REFERENCES service_instances(id),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    order_type VARCHAR(50) NOT NULL CHECK (order_type IN ('NEW_SERVICE', 'MODIFY_SERVICE', 'TERMINATE_SERVICE')),
    requested_bandwidth_mbps INTEGER,
    installation_address TEXT,
    postal_code VARCHAR(20),
    contact_person VARCHAR(255),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(255),
    requested_date DATE,
    estimated_completion_date DATE,
    actual_completion_date DATE,
    status VARCHAR(50) DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED', 'APPROVED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'FAILED')),
    total_cost DECIMAL(10,2),
    notes TEXT,
    workflow_id VARCHAR(255), -- Reference to Service Orchestrator workflow
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Bandwidth change history
CREATE TABLE bandwidth_changes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_instance_id UUID NOT NULL REFERENCES service_instances(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    previous_bandwidth_mbps INTEGER NOT NULL,
    new_bandwidth_mbps INTEGER NOT NULL,
    change_reason VARCHAR(255),
    scheduled_at TIMESTAMP WITH TIME ZONE,
    applied_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SCHEDULED', 'APPLIED', 'FAILED', 'CANCELLED')),
    cost_impact DECIMAL(10,2),
    workflow_id VARCHAR(255), -- Reference to Service Orchestrator workflow
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_company_id ON users(company_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_service_instances_company_id ON service_instances(company_id);
CREATE INDEX idx_service_instances_service_id ON service_instances(service_id);
CREATE INDEX idx_service_instances_status ON service_instances(status);
CREATE INDEX idx_orders_company_id ON orders(company_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_bandwidth_changes_service_instance_id ON bandwidth_changes(service_instance_id);
CREATE INDEX idx_bandwidth_changes_status ON bandwidth_changes(status);
CREATE INDEX idx_services_category_id ON services(category_id);
CREATE INDEX idx_services_service_type ON services(service_type);
CREATE INDEX idx_services_is_available ON services(is_available);

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply updated_at triggers to all tables
CREATE TRIGGER update_companies_updated_at BEFORE UPDATE ON companies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_service_categories_updated_at BEFORE UPDATE ON service_categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_services_updated_at BEFORE UPDATE ON services FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_service_instances_updated_at BEFORE UPDATE ON service_instances FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_bandwidth_changes_updated_at BEFORE UPDATE ON bandwidth_changes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
