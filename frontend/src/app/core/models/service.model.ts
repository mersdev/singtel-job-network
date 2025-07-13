export interface Service {
  id: string;
  name: string;
  description: string;
  category: ServiceCategory;
  type: ServiceType;
  status: ServiceStatus;
  pricing: ServicePricing;
  features: string[];
  specifications: ServiceSpecification;
  availability: ServiceAvailability;
  createdAt: Date;
  updatedAt: Date;
}

export enum ServiceCategory {
  CONNECTIVITY = 'connectivity',
  SECURITY = 'security',
  CLOUD = 'cloud',
  VOICE = 'voice',
  DATA = 'data'
}

export enum ServiceType {
  INTERNET = 'internet',
  MPLS = 'mpls',
  ETHERNET = 'ethernet',
  FIREWALL = 'firewall',
  VPN = 'vpn',
  VOICE_OVER_IP = 'voip',
  CLOUD_STORAGE = 'cloud_storage'
}

export enum ServiceStatus {
  ACTIVE = 'active',
  INACTIVE = 'inactive',
  PROVISIONING = 'provisioning',
  MAINTENANCE = 'maintenance',
  ERROR = 'error'
}

export interface ServicePricing {
  basePrice: number;
  currency: string;
  billingCycle: 'monthly' | 'quarterly' | 'annually';
  setupFee?: number;
  bandwidthTiers?: BandwidthTier[];
}

export interface BandwidthTier {
  minBandwidth: number;
  maxBandwidth: number;
  pricePerMbps: number;
}

export interface ServiceSpecification {
  bandwidth?: {
    min: number;
    max: number;
    unit: 'Mbps' | 'Gbps';
  };
  latency?: {
    typical: number;
    maximum: number;
    unit: 'ms';
  };
  uptime?: {
    sla: number; // percentage
    penalty?: number;
  };
  locations?: string[];
}

export interface ServiceAvailability {
  regions: string[];
  datacenters: string[];
  supportedProtocols?: string[];
}

export interface ServiceOrder {
  id: string;
  serviceId: string;
  service: Service;
  customerId: string;
  configuration: ServiceConfiguration;
  status: OrderStatus;
  totalCost: number;
  estimatedDelivery: Date;
  createdAt: Date;
  updatedAt: Date;
  notes?: string;
}

export interface ServiceConfiguration {
  bandwidth?: number;
  location?: string;
  redundancy?: boolean;
  monitoring?: boolean;
  customSettings?: Record<string, any>;
}

export enum OrderStatus {
  PENDING = 'pending',
  APPROVED = 'approved',
  PROVISIONING = 'provisioning',
  ACTIVE = 'active',
  CANCELLED = 'cancelled',
  FAILED = 'failed'
}

export interface ServiceFilters {
  category?: ServiceCategory[];
  type?: ServiceType[];
  status?: ServiceStatus[];
  priceRange?: {
    min: number;
    max: number;
  };
  bandwidthRange?: {
    min: number;
    max: number;
  };
  regions?: string[];
  searchTerm?: string;
}
