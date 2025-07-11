-- Service monitoring and metrics tables
-- Version: 2.0
-- Description: Tables for storing service monitoring data and metrics

-- Set schema
SET search_path TO singtel_app;

-- Service metrics table - Store real-time and historical metrics
CREATE TABLE service_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_instance_id UUID NOT NULL REFERENCES service_instances(id) ON DELETE CASCADE,
    metric_type VARCHAR(50) NOT NULL, -- 'BANDWIDTH_UTILIZATION', 'LATENCY', 'PACKET_LOSS', 'UPTIME'
    metric_value DECIMAL(15,6) NOT NULL,
    metric_unit VARCHAR(20), -- 'MBPS', 'MS', 'PERCENT', etc.
    measured_at TIMESTAMP WITH TIME ZONE NOT NULL,
    source_system VARCHAR(100), -- Which monitoring system provided this data
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Service status events - Track service status changes and incidents
CREATE TABLE service_status_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_instance_id UUID NOT NULL REFERENCES service_instances(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL, -- 'STATUS_CHANGE', 'INCIDENT', 'MAINTENANCE'
    previous_status VARCHAR(50),
    new_status VARCHAR(50),
    severity VARCHAR(20) DEFAULT 'INFO' CHECK (severity IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'INFO')),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE,
    impact_description TEXT,
    root_cause TEXT,
    resolution_notes TEXT,
    created_by VARCHAR(100), -- System or user who created the event
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Service alerts - Configuration for monitoring alerts
CREATE TABLE service_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_instance_id UUID NOT NULL REFERENCES service_instances(id) ON DELETE CASCADE,
    alert_name VARCHAR(255) NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    threshold_value DECIMAL(15,6) NOT NULL,
    threshold_operator VARCHAR(10) NOT NULL CHECK (threshold_operator IN ('>', '<', '>=', '<=', '=')),
    severity VARCHAR(20) DEFAULT 'MEDIUM' CHECK (severity IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW')),
    is_enabled BOOLEAN DEFAULT true,
    notification_emails TEXT[], -- Array of email addresses
    notification_webhooks TEXT[], -- Array of webhook URLs
    cooldown_minutes INTEGER DEFAULT 15, -- Minimum time between alerts
    last_triggered_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Alert notifications - Log of sent alert notifications
CREATE TABLE alert_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alert_id UUID NOT NULL REFERENCES service_alerts(id) ON DELETE CASCADE,
    service_instance_id UUID NOT NULL REFERENCES service_instances(id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL, -- 'EMAIL', 'WEBHOOK', 'SMS'
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'FAILED', 'DELIVERED')),
    sent_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Service SLA metrics - Track SLA compliance
CREATE TABLE service_sla_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_instance_id UUID NOT NULL REFERENCES service_instances(id) ON DELETE CASCADE,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    uptime_percentage DECIMAL(5,2),
    availability_percentage DECIMAL(5,2),
    average_latency_ms DECIMAL(10,3),
    packet_loss_percentage DECIMAL(5,4),
    bandwidth_utilization_avg DECIMAL(5,2),
    bandwidth_utilization_peak DECIMAL(5,2),
    incidents_count INTEGER DEFAULT 0,
    maintenance_hours DECIMAL(6,2) DEFAULT 0,
    sla_compliance_score DECIMAL(5,2), -- Overall SLA score
    calculated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Network topology - Store network path information for services
CREATE TABLE network_topology (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_instance_id UUID NOT NULL REFERENCES service_instances(id) ON DELETE CASCADE,
    node_type VARCHAR(50) NOT NULL, -- 'ROUTER', 'SWITCH', 'FIREWALL', 'ENDPOINT'
    node_id VARCHAR(100) NOT NULL,
    node_name VARCHAR(255),
    node_location VARCHAR(255),
    ip_address INET,
    mac_address MACADDR,
    vendor VARCHAR(100),
    model VARCHAR(100),
    firmware_version VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')),
    last_seen_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for monitoring tables
CREATE INDEX idx_service_metrics_service_instance_id ON service_metrics(service_instance_id);
CREATE INDEX idx_service_metrics_metric_type ON service_metrics(metric_type);
CREATE INDEX idx_service_metrics_measured_at ON service_metrics(measured_at);
CREATE INDEX idx_service_metrics_composite ON service_metrics(service_instance_id, metric_type, measured_at);

CREATE INDEX idx_service_status_events_service_instance_id ON service_status_events(service_instance_id);
CREATE INDEX idx_service_status_events_event_type ON service_status_events(event_type);
CREATE INDEX idx_service_status_events_started_at ON service_status_events(started_at);
CREATE INDEX idx_service_status_events_severity ON service_status_events(severity);

CREATE INDEX idx_service_alerts_service_instance_id ON service_alerts(service_instance_id);
CREATE INDEX idx_service_alerts_is_enabled ON service_alerts(is_enabled);
CREATE INDEX idx_service_alerts_metric_type ON service_alerts(metric_type);

CREATE INDEX idx_alert_notifications_alert_id ON alert_notifications(alert_id);
CREATE INDEX idx_alert_notifications_status ON alert_notifications(status);
CREATE INDEX idx_alert_notifications_sent_at ON alert_notifications(sent_at);

CREATE INDEX idx_service_sla_metrics_service_instance_id ON service_sla_metrics(service_instance_id);
CREATE INDEX idx_service_sla_metrics_period ON service_sla_metrics(period_start, period_end);

CREATE INDEX idx_network_topology_service_instance_id ON network_topology(service_instance_id);
CREATE INDEX idx_network_topology_node_type ON network_topology(node_type);
CREATE INDEX idx_network_topology_status ON network_topology(status);

-- Apply updated_at triggers to new tables
CREATE TRIGGER update_service_status_events_updated_at BEFORE UPDATE ON service_status_events FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_service_alerts_updated_at BEFORE UPDATE ON service_alerts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_network_topology_updated_at BEFORE UPDATE ON network_topology FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Note: Partitioning for service_metrics table can be added later for better performance
-- This will help with large volumes of monitoring data when needed
