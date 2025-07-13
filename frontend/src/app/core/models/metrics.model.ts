export interface ServiceMetrics {
  serviceId: string;
  serviceName: string;
  timestamp: Date;
  bandwidth: BandwidthMetrics;
  performance: PerformanceMetrics;
  availability: AvailabilityMetrics;
  usage: UsageMetrics;
}

export interface BandwidthMetrics {
  current: number;
  allocated: number;
  peak: number;
  average: number;
  unit: 'Mbps' | 'Gbps';
  utilization: number; // percentage
  history: DataPoint[];
}

export interface PerformanceMetrics {
  latency: {
    current: number;
    average: number;
    peak: number;
    unit: 'ms';
  };
  packetLoss: {
    current: number;
    average: number;
    unit: '%';
  };
  jitter: {
    current: number;
    average: number;
    unit: 'ms';
  };
}

export interface AvailabilityMetrics {
  uptime: {
    current: number; // percentage
    monthly: number;
    yearly: number;
    sla: number;
  };
  incidents: Incident[];
  maintenanceWindows: MaintenanceWindow[];
}

export interface UsageMetrics {
  dataTransfer: {
    inbound: number;
    outbound: number;
    total: number;
    unit: 'GB' | 'TB';
  };
  connections: {
    active: number;
    peak: number;
    total: number;
  };
  costs: {
    current: number;
    projected: number;
    currency: string;
  };
}

export interface DataPoint {
  timestamp: Date;
  value: number;
}

export interface Incident {
  id: string;
  title: string;
  description: string;
  severity: 'low' | 'medium' | 'high' | 'critical';
  status: 'open' | 'investigating' | 'resolved';
  startTime: Date;
  endTime?: Date;
  impact: string[];
}

export interface MaintenanceWindow {
  id: string;
  title: string;
  description: string;
  scheduledStart: Date;
  scheduledEnd: Date;
  actualStart?: Date;
  actualEnd?: Date;
  status: 'scheduled' | 'in-progress' | 'completed' | 'cancelled';
  affectedServices: string[];
}

export interface Alert {
  id: string;
  serviceId: string;
  serviceName: string;
  type: AlertType;
  severity: AlertSeverity;
  message: string;
  timestamp: Date;
  acknowledged: boolean;
  resolvedAt?: Date;
  metadata?: Record<string, any>;
}

export enum AlertType {
  BANDWIDTH_THRESHOLD = 'bandwidth_threshold',
  LATENCY_HIGH = 'latency_high',
  PACKET_LOSS = 'packet_loss',
  SERVICE_DOWN = 'service_down',
  MAINTENANCE = 'maintenance',
  SECURITY = 'security'
}

export enum AlertSeverity {
  INFO = 'info',
  WARNING = 'warning',
  ERROR = 'error',
  CRITICAL = 'critical'
}

export interface DashboardMetrics {
  totalServices: number;
  activeServices: number;
  totalBandwidth: number;
  utilizationRate: number;
  monthlySpend: number;
  alerts: Alert[];
  recentOrders: number;
  uptime: number;
}
