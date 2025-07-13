-- Comprehensive mock data for testing and development
-- Version: 5.0
-- Description: Add more services and orders to ensure comprehensive test coverage

-- Set schema
SET search_path TO singtel_app;

-- Add more diverse services to existing categories
INSERT INTO services (id, category_id, name, description, service_type, base_bandwidth_mbps, max_bandwidth_mbps, min_bandwidth_mbps, base_price_monthly, price_per_mbps, setup_fee, contract_term_months, is_bandwidth_adjustable, is_available, provisioning_time_hours, features, technical_specs) VALUES

-- More Business Internet services
('660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 'Business Fiber 100M', 'Entry-level fiber internet for small businesses', 'FIBER', 100, 500, 50, 149.00, 0.80, 100.00, 12, true, true, 48, 
 '{"static_ip": false, "sla_uptime": "99.5%", "support_level": "business_hours", "backup_connection": false}',
 '{"technology": "GPON", "interface": "Ethernet", "redundancy": "single_path"}'),

('660e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 'Business Fiber 2G', 'Ultra-high-speed fiber for large enterprises', 'FIBER', 2000, 5000, 1000, 899.00, 0.30, 300.00, 36, true, true, 120, 
 '{"static_ip": true, "sla_uptime": "99.99%", "support_level": "24x7", "backup_connection": true}',
 '{"technology": "10G_EPON", "interface": "10GE", "redundancy": "dual_path"}'),

-- More VPN Services
('660e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440002', 'Cloud VPN Basic', 'Basic cloud-based VPN solution', 'VPN', 25, 100, 10, 79.00, 1.50, 25.00, 12, true, true, 4, 
 '{"encryption": "AES-256", "protocols": ["IPSec", "OpenVPN"], "concurrent_users": 10, "cloud_based": true}',
 '{"gateway_type": "cloud", "bandwidth_guarantee": false, "failover": "automatic"}'),

('660e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440002', 'Enterprise VPN Premium', 'Premium VPN with advanced security features', 'VPN', 200, 1000, 100, 399.00, 0.75, 150.00, 24, true, true, 48, 
 '{"encryption": "AES-256", "protocols": ["IPSec", "SSL", "MPLS"], "concurrent_users": 100, "cloud_based": false}',
 '{"gateway_type": "dedicated", "bandwidth_guarantee": true, "failover": "instant"}'),

-- More Dedicated Lines
('660e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440003', 'Dedicated Internet 50M', 'Dedicated internet access for small enterprises', 'DEDICATED', 50, 50, 50, 599.00, 0.00, 300.00, 24, false, true, 120, 
 '{"sla_uptime": "99.95%", "support_level": "24x7", "backup_connection": false, "symmetric": true}',
 '{"technology": "Ethernet", "interface": "FastEthernet", "redundancy": "single_path"}'),

('660e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440003', 'Dedicated Internet 500M', 'High-capacity dedicated internet', 'DEDICATED', 500, 500, 500, 1299.00, 0.00, 800.00, 36, false, true, 168, 
 '{"sla_uptime": "99.99%", "support_level": "24x7", "backup_connection": true, "symmetric": true}',
 '{"technology": "Ethernet", "interface": "GigabitEthernet", "redundancy": "dual_path"}'),

-- Cloud Connect services
('660e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440004', 'AWS Direct Connect 100M', 'Direct connection to AWS cloud services', 'CLOUD_CONNECT', 100, 1000, 50, 249.00, 1.20, 200.00, 12, true, true, 72, 
 '{"cloud_provider": "AWS", "regions": ["ap-southeast-1"], "vlan_support": true, "bgp_routing": true}',
 '{"technology": "Direct_Connect", "interface": "Ethernet", "routing_protocol": "BGP"}'),

('660e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440004', 'Azure ExpressRoute 200M', 'Direct connection to Microsoft Azure', 'CLOUD_CONNECT', 200, 2000, 100, 399.00, 1.00, 250.00, 24, true, true, 96, 
 '{"cloud_provider": "Azure", "regions": ["Southeast Asia"], "vlan_support": true, "bgp_routing": true}',
 '{"technology": "ExpressRoute", "interface": "Ethernet", "routing_protocol": "BGP"}'),

('660e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440004', 'Google Cloud Interconnect 500M', 'Direct connection to Google Cloud Platform', 'CLOUD_CONNECT', 500, 5000, 250, 699.00, 0.80, 400.00, 36, true, true, 120,
 '{"cloud_provider": "GCP", "regions": ["asia-southeast1"], "vlan_support": true, "bgp_routing": true}',
 '{"technology": "Cloud_Interconnect", "interface": "Ethernet", "routing_protocol": "BGP"}');

-- Add more service instances for testing
INSERT INTO service_instances (id, company_id, service_id, instance_name, current_bandwidth_mbps, installation_address, postal_code, contact_person, contact_phone, contact_email, status, monthly_cost, contract_start_date, contract_end_date) VALUES

-- TechStart additional services
('990e8400-e29b-41d4-a716-446655440005', '770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440008', 'TechStart Cloud VPN', 25, '1 Marina Bay Sands, Level 10', '018956', 'John Doe', '+65 9123 4567', 'john.doe@techstart.sg', 'ACTIVE', 79.00, '2024-03-01', '2025-02-28'),

('990e8400-e29b-41d4-a716-446655440006', '770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440012', 'TechStart AWS Connection', 100, '1 Marina Bay Sands, Level 10', '018956', 'John Doe', '+65 9123 4567', 'john.doe@techstart.sg', 'PROVISIONING', 249.00, '2024-07-01', '2025-06-30'),

-- Global Trading additional services
('990e8400-e29b-41d4-a716-446655440007', '770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440009', 'Global Trading VPN Premium', 200, '50 Raffles Place, #20-01', '048623', 'Mike Wilson', '+65 9345 6789', 'mike.wilson@globaltrading.com.sg', 'ACTIVE', 399.00, '2024-02-01', '2026-01-31'),

('990e8400-e29b-41d4-a716-446655440008', '770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440011', 'Global Trading Dedicated 500M', 500, '50 Raffles Place, #20-01', '048623', 'Mike Wilson', '+65 9345 6789', 'mike.wilson@globaltrading.com.sg', 'ACTIVE', 1299.00, '2024-04-01', '2027-03-31'),

-- Manufacturing Solutions additional services
('990e8400-e29b-41d4-a716-446655440009', '770e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440006', 'Manufacturing Backup Internet', 100, '123 Jurong Industrial Estate', '629734', 'David Tan', '+65 9567 8901', 'david.tan@manufacturing.sg', 'ACTIVE', 149.00, '2024-05-01', '2025-04-30');

-- Add comprehensive orders covering all order types and statuses
INSERT INTO orders (id, company_id, user_id, service_id, service_instance_id, order_number, order_type, requested_bandwidth_mbps, installation_address, postal_code, contact_person, contact_phone, contact_email, requested_date, estimated_completion_date, actual_completion_date, status, total_cost, notes) VALUES

-- NEW_SERVICE orders with different statuses
('aa0e8400-e29b-41d4-a716-446655440003', '770e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440008', '990e8400-e29b-41d4-a716-446655440005', 'ORD-2024-003', 'NEW_SERVICE', 25, '1 Marina Bay Sands, Level 10', '018956', 'John Doe', '+65 9123 4567', 'john.doe@techstart.sg', '2024-02-15', '2024-03-01', '2024-03-01', 'COMPLETED', 104.00, 'Cloud VPN for remote workers'),

('aa0e8400-e29b-41d4-a716-446655440004', '770e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440012', '990e8400-e29b-41d4-a716-446655440006', 'ORD-2024-004', 'NEW_SERVICE', 100, '1 Marina Bay Sands, Level 10', '018956', 'John Doe', '+65 9123 4567', 'john.doe@techstart.sg', '2024-06-15', '2024-07-01', NULL, 'IN_PROGRESS', 449.00, 'AWS Direct Connect for cloud migration'),

('aa0e8400-e29b-41d4-a716-446655440005', '770e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440007', NULL, 'ORD-2024-005', 'NEW_SERVICE', 2000, '50 Raffles Place, #20-01', '048623', 'Mike Wilson', '+65 9345 6789', 'mike.wilson@globaltrading.com.sg', '2024-07-01', '2024-08-15', NULL, 'SUBMITTED', 1199.00, 'Ultra-high-speed fiber for data center'),

('aa0e8400-e29b-41d4-a716-446655440006', '770e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440010', NULL, 'ORD-2024-006', 'NEW_SERVICE', 50, '123 Jurong Industrial Estate', '629734', 'David Tan', '+65 9567 8901', 'david.tan@manufacturing.sg', '2024-06-20', '2024-08-01', NULL, 'APPROVED', 899.00, 'Dedicated line for critical operations'),

-- MODIFY_SERVICE orders
('aa0e8400-e29b-41d4-a716-446655440007', '770e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', '990e8400-e29b-41d4-a716-446655440001', 'ORD-2024-007', 'MODIFY_SERVICE', 750, '1 Marina Bay Sands, Level 10', '018956', 'John Doe', '+65 9123 4567', 'john.doe@techstart.sg', '2024-05-15', '2024-06-01', '2024-06-01', 'COMPLETED', 125.00, 'Bandwidth upgrade for peak season'),

('aa0e8400-e29b-41d4-a716-446655440008', '770e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440009', '990e8400-e29b-41d4-a716-446655440007', 'ORD-2024-008', 'MODIFY_SERVICE', 500, '50 Raffles Place, #20-01', '048623', 'Mike Wilson', '+65 9345 6789', 'mike.wilson@globaltrading.com.sg', '2024-06-01', '2024-06-15', NULL, 'IN_PROGRESS', 187.50, 'VPN bandwidth increase'),

-- TERMINATE_SERVICE orders
('aa0e8400-e29b-41d4-a716-446655440009', '770e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440006', '990e8400-e29b-41d4-a716-446655440009', 'ORD-2024-009', 'TERMINATE_SERVICE', NULL, '123 Jurong Industrial Estate', '629734', 'David Tan', '+65 9567 8901', 'david.tan@manufacturing.sg', '2024-07-01', '2024-07-15', NULL, 'SUBMITTED', 0.00, 'Terminating backup internet service'),

('aa0e8400-e29b-41d4-a716-446655440010', '770e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440004', '660e8400-e29b-41d4-a716-446655440003', '990e8400-e29b-41d4-a716-446655440003', 'ORD-2024-010', 'TERMINATE_SERVICE', NULL, '50 Raffles Place, #20-01', '048623', 'Sarah Chen', '+65 9456 7890', 'sarah.chen@globaltrading.com.sg', '2024-05-01', '2024-05-15', '2024-05-15', 'COMPLETED', 0.00, 'Service termination due to office relocation'),

-- Failed and cancelled orders
('aa0e8400-e29b-41d4-a716-446655440011', '770e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440013', NULL, 'ORD-2024-011', 'NEW_SERVICE', 200, '1 Marina Bay Sands, Level 11', '018956', 'Jane Smith', '+65 9234 5678', 'jane.smith@techstart.sg', '2024-04-01', '2024-05-01', NULL, 'FAILED', 649.00, 'Installation failed due to infrastructure limitations'),

('aa0e8400-e29b-41d4-a716-446655440012', '770e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440014', NULL, 'ORD-2024-012', 'NEW_SERVICE', 500, '123 Jurong Industrial Estate', '629734', 'David Tan', '+65 9567 8901', 'david.tan@manufacturing.sg', '2024-03-15', '2024-05-01', NULL, 'CANCELLED', 1099.00, 'Order cancelled due to budget constraints');
