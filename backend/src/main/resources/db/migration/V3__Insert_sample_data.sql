-- Sample data for development and testing
-- Version: 3.0
-- Description: Insert sample data for service categories, services, companies, and users

-- Set schema
SET search_path TO singtel_app;

-- Insert service categories
INSERT INTO service_categories (id, name, description, display_order, is_active) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Business Internet', 'High-speed internet connectivity for businesses', 1, true),
('550e8400-e29b-41d4-a716-446655440002', 'VPN Services', 'Secure virtual private network solutions', 2, true),
('550e8400-e29b-41d4-a716-446655440003', 'Dedicated Lines', 'Dedicated point-to-point connectivity', 3, true),
('550e8400-e29b-41d4-a716-446655440004', 'Cloud Connect', 'Direct cloud connectivity services', 4, true);

-- Insert sample services
INSERT INTO services (id, category_id, name, description, service_type, base_bandwidth_mbps, max_bandwidth_mbps, min_bandwidth_mbps, base_price_monthly, price_per_mbps, setup_fee, contract_term_months, is_bandwidth_adjustable, is_available, provisioning_time_hours, features, technical_specs) VALUES
-- Business Fiber services
('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Business Fiber 500M', 'High-speed fiber internet with 500 Mbps download and upload', 'FIBER', 500, 1000, 100, 299.00, 0.50, 150.00, 24, true, true, 72, 
 '{"static_ip": true, "sla_uptime": "99.9%", "support_level": "24x7", "backup_connection": false}',
 '{"technology": "fiber_optic", "latency_ms": 5, "jitter_ms": 1, "packet_loss_max": 0.1}'),

('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Business Fiber 1G', 'Ultra-high-speed fiber internet with 1 Gbps symmetrical', 'FIBER', 1000, 2000, 500, 499.00, 0.40, 200.00, 24, true, true, 72,
 '{"static_ip": true, "sla_uptime": "99.95%", "support_level": "24x7", "backup_connection": true}',
 '{"technology": "fiber_optic", "latency_ms": 3, "jitter_ms": 0.5, "packet_loss_max": 0.05}'),

-- VPN services
('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', 'Site-to-Site VPN', 'Secure connection between office locations', 'VPN', 100, 500, 50, 199.00, 1.00, 100.00, 12, true, true, 24,
 '{"encryption": "AES-256", "protocols": ["IPSec", "OpenVPN"], "concurrent_tunnels": 10, "support_level": "business_hours"}',
 '{"encryption_strength": "256-bit", "authentication": "certificate_based", "redundancy": "active_passive"}'),

('660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', 'Remote Access VPN', 'Secure remote access for employees', 'VPN', 50, 200, 25, 99.00, 2.00, 50.00, 12, true, true, 4,
 '{"encryption": "AES-256", "concurrent_users": 50, "mobile_support": true, "support_level": "business_hours"}',
 '{"client_software": "multi_platform", "two_factor_auth": true, "session_timeout": 8}'),

-- Dedicated line services
('660e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440003', 'Dedicated Internet 100M', 'Dedicated internet access with guaranteed bandwidth', 'DEDICATED', 100, 100, 100, 899.00, 0.00, 500.00, 36, false, true, 168,
 '{"guaranteed_bandwidth": true, "sla_uptime": "99.99%", "support_level": "24x7", "burstable": false}',
 '{"technology": "ethernet", "latency_ms": 2, "jitter_ms": 0.2, "packet_loss_max": 0.01}');

-- Insert sample companies
INSERT INTO companies (id, name, registration_number, email, phone, address, postal_code, country, industry, company_size, status) VALUES
('770e8400-e29b-41d4-a716-446655440001', 'TechStart Pte Ltd', '202301234A', 'admin@techstart.sg', '+65 6123 4567', '1 Marina Bay Sands, Level 10', '018956', 'Singapore', 'Technology', 'SMALL', 'ACTIVE'),
('770e8400-e29b-41d4-a716-446655440002', 'Global Trading Co', '201912345B', 'it@globaltrading.com.sg', '+65 6234 5678', '50 Raffles Place, #20-01', '048623', 'Singapore', 'Trading', 'MEDIUM', 'ACTIVE'),
('770e8400-e29b-41d4-a716-446655440003', 'Manufacturing Solutions', '200812345C', 'network@manufacturing.sg', '+65 6345 6789', '123 Jurong Industrial Estate', '629734', 'Singapore', 'Manufacturing', 'LARGE', 'ACTIVE');

-- Insert sample users (passwords are hashed for 'password123')
INSERT INTO users (id, company_id, username, email, password_hash, first_name, last_name, phone, role, status) VALUES
-- TechStart users
('880e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001', 'john.doe', 'john.doe@techstart.sg', '$2a$10$FthcQPUtqPvKREhD8hP7jO7HPFAybHRpu2ZoNLMzZ9A2HmmO.H2Qe', 'John', 'Doe', '+65 9123 4567', 'ADMIN', 'ACTIVE'),
('880e8400-e29b-41d4-a716-446655440002', '770e8400-e29b-41d4-a716-446655440001', 'jane.smith', 'jane.smith@techstart.sg', '$2a$10$FthcQPUtqPvKREhD8hP7jO7HPFAybHRpu2ZoNLMzZ9A2HmmO.H2Qe', 'Jane', 'Smith', '+65 9234 5678', 'USER', 'ACTIVE'),

-- Global Trading users
('880e8400-e29b-41d4-a716-446655440003', '770e8400-e29b-41d4-a716-446655440002', 'mike.wilson', 'mike.wilson@globaltrading.com.sg', '$2a$10$FthcQPUtqPvKREhD8hP7jO7HPFAybHRpu2ZoNLMzZ9A2HmmO.H2Qe', 'Mike', 'Wilson', '+65 9345 6789', 'ADMIN', 'ACTIVE'),
('880e8400-e29b-41d4-a716-446655440004', '770e8400-e29b-41d4-a716-446655440002', 'sarah.lee', 'sarah.lee@globaltrading.com.sg', '$2a$10$FthcQPUtqPvKREhD8hP7jO7HPFAybHRpu2ZoNLMzZ9A2HmmO.H2Qe', 'Sarah', 'Lee', '+65 9456 7890', 'USER', 'ACTIVE'),

-- Manufacturing Solutions users
('880e8400-e29b-41d4-a716-446655440005', '770e8400-e29b-41d4-a716-446655440003', 'david.tan', 'david.tan@manufacturing.sg', '$2a$10$FthcQPUtqPvKREhD8hP7jO7HPFAybHRpu2ZoNLMzZ9A2HmmO.H2Qe', 'David', 'Tan', '+65 9567 8901', 'ADMIN', 'ACTIVE');

-- Insert sample service instances
INSERT INTO service_instances (id, company_id, service_id, instance_name, current_bandwidth_mbps, installation_address, postal_code, contact_person, contact_phone, contact_email, status, monthly_cost, contract_start_date, contract_end_date) VALUES
-- TechStart services
('990e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', 'TechStart Main Office Internet', 500, '1 Marina Bay Sands, Level 10', '018956', 'John Doe', '+65 9123 4567', 'john.doe@techstart.sg', 'ACTIVE', 299.00, '2024-01-01', '2025-12-31'),

-- Global Trading services
('990e8400-e29b-41d4-a716-446655440002', '770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440002', 'Global Trading HQ Connection', 1000, '50 Raffles Place, #20-01', '048623', 'Mike Wilson', '+65 9345 6789', 'mike.wilson@globaltrading.com.sg', 'ACTIVE', 499.00, '2024-02-01', '2026-01-31'),
('990e8400-e29b-41d4-a716-446655440003', '770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440003', 'Branch Office VPN', 100, '100 Orchard Road, #15-01', '238840', 'Sarah Lee', '+65 9456 7890', 'sarah.lee@globaltrading.com.sg', 'ACTIVE', 199.00, '2024-03-01', '2025-02-28'),

-- Manufacturing Solutions services
('990e8400-e29b-41d4-a716-446655440004', '770e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440005', 'Manufacturing Plant Dedicated Line', 100, '123 Jurong Industrial Estate', '629734', 'David Tan', '+65 9567 8901', 'david.tan@manufacturing.sg', 'ACTIVE', 899.00, '2024-01-15', '2027-01-14');

-- Insert sample orders
INSERT INTO orders (id, company_id, user_id, service_id, service_instance_id, order_number, order_type, requested_bandwidth_mbps, installation_address, postal_code, contact_person, contact_phone, contact_email, requested_date, estimated_completion_date, actual_completion_date, status, total_cost, notes) VALUES
('aa0e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', '990e8400-e29b-41d4-a716-446655440001', 'ORD-2024-001', 'NEW_SERVICE', 500, '1 Marina Bay Sands, Level 10', '018956', 'John Doe', '+65 9123 4567', 'john.doe@techstart.sg', '2023-12-15', '2024-01-01', '2024-01-01', 'COMPLETED', 449.00, 'Initial service setup for new office'),

('aa0e8400-e29b-41d4-a716-446655440002', '770e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440002', '990e8400-e29b-41d4-a716-446655440002', 'ORD-2024-002', 'NEW_SERVICE', 1000, '50 Raffles Place, #20-01', '048623', 'Mike Wilson', '+65 9345 6789', 'mike.wilson@globaltrading.com.sg', '2024-01-15', '2024-02-01', '2024-02-01', 'COMPLETED', 699.00, 'Upgrade to 1Gbps for increased capacity');

-- Insert sample bandwidth changes
INSERT INTO bandwidth_changes (id, service_instance_id, user_id, previous_bandwidth_mbps, new_bandwidth_mbps, change_reason, scheduled_at, applied_at, status, cost_impact) VALUES
('bb0e8400-e29b-41d4-a716-446655440001', '990e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', 500, 750, 'Temporary increase for product launch', '2024-06-01 09:00:00+08', '2024-06-01 09:15:00+08', 'APPLIED', 125.00),
('bb0e8400-e29b-41d4-a716-446655440002', '990e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', 750, 500, 'Return to normal after event', '2024-06-15 18:00:00+08', '2024-06-15 18:05:00+08', 'APPLIED', -125.00);

-- Insert sample service metrics (last 24 hours)
INSERT INTO service_metrics (service_instance_id, metric_type, metric_value, metric_unit, measured_at, source_system) 
SELECT 
    '990e8400-e29b-41d4-a716-446655440001',
    'BANDWIDTH_UTILIZATION',
    random() * 80 + 10, -- Random utilization between 10-90%
    'PERCENT',
    CURRENT_TIMESTAMP - (interval '1 hour' * generate_series(1, 24)),
    'NETWORK_MONITOR'
FROM generate_series(1, 24);

-- Insert sample SLA metrics
INSERT INTO service_sla_metrics (service_instance_id, period_start, period_end, uptime_percentage, availability_percentage, average_latency_ms, packet_loss_percentage, bandwidth_utilization_avg, bandwidth_utilization_peak, incidents_count, sla_compliance_score) VALUES
('990e8400-e29b-41d4-a716-446655440001', '2024-06-01', '2024-06-30', 99.95, 99.95, 4.2, 0.02, 45.6, 89.3, 1, 99.8),
('990e8400-e29b-41d4-a716-446655440002', '2024-06-01', '2024-06-30', 99.98, 99.98, 2.8, 0.01, 62.1, 95.7, 0, 99.9),
('990e8400-e29b-41d4-a716-446655440003', '2024-06-01', '2024-06-30', 99.92, 99.92, 8.5, 0.05, 38.4, 78.2, 2, 99.5);
