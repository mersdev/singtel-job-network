import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, retry, catchError } from 'rxjs/operators';
import { HttpParams } from '@angular/common/http';
import { BaseApiService, ApiResponse, PaginatedResponse } from './base-api.service';

export interface CreateOrderRequest {
  serviceId: string;
  orderType: OrderType;
  requestedBandwidthMbps?: number;
  installationAddress: string;
  postalCode: string;
  contactPerson: string;
  contactPhone: string;
  contactEmail: string;
  requestedDate: string; // ISO date string
  specialRequirements?: string;
  configuration?: Record<string, any>;
}

export interface OrderResponse {
  id: string;
  orderNumber: string;
  orderType: OrderType;
  status: OrderStatus;
  requestedBandwidthMbps?: number;
  installationAddress: string;
  postalCode: string;
  contactPerson: string;
  contactPhone: string;
  contactEmail: string;
  requestedDate: string;
  estimatedCompletionDate?: string;
  actualCompletionDate?: string;
  specialRequirements?: string;
  totalCost: number;
  notes?: string;
  workflowId?: string;
  createdAt: string;
  updatedAt: string;
  pending?: boolean;
  completed?: boolean;
  service: {
    id: string;
    name: string;
    serviceType: string;
  };
  serviceInstance?: {
    id: string;
    instanceName: string;
    status: string;
  };
  user: {
    id: string;
    username: string;
    fullName: string;
  };
  company: {
    id: string;
    name: string;
  };
}

export enum OrderType {
  NEW_SERVICE = 'NEW_SERVICE',
  UPGRADE = 'UPGRADE',
  DOWNGRADE = 'DOWNGRADE',
  CANCELLATION = 'CANCELLATION',
  MODIFICATION = 'MODIFICATION'
}

export enum OrderStatus {
  SUBMITTED = 'SUBMITTED',
  APPROVED = 'APPROVED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  FAILED = 'FAILED'
}

export interface OrderSearchParams {
  status?: OrderStatus;
  orderType?: OrderType;
  serviceId?: string;
  startDate?: string;
  endDate?: string;
  minCost?: number;
  maxCost?: number;
  page?: number;
  limit?: number;
}

export interface OrderStatistics {
  totalOrderValue: number;
  pendingOrdersCount: number;
  recentOrdersCount: number;
  currency: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService extends BaseApiService {

  /**
   * Create a new order
   */
  createOrder(request: CreateOrderRequest): Observable<OrderResponse> {
    return this.post<OrderResponse>('orders', request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get order by ID
   */
  getOrderById(orderId: string): Observable<OrderResponse> {
    return this.get<OrderResponse>(`orders/${orderId}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get order by order number
   */
  getOrderByNumber(orderNumber: string): Observable<OrderResponse> {
    return this.get<OrderResponse>(`orders/number/${orderNumber}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get all orders for current user's company
   */
  getCompanyOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/orders`).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  /**
   * Get orders with pagination
   */
  getOrdersPaged(page: number = 1, limit: number = 20): Observable<PaginatedResponse<OrderResponse>> {
    return this.getPaginated<OrderResponse>('orders/paged', page, limit);
  }

  /**
   * Search orders
   */
  searchOrders(searchParams: OrderSearchParams): Observable<PaginatedResponse<OrderResponse>> {
    let params = new HttpParams();
    
    if (searchParams.status) {
      params = params.set('status', searchParams.status);
    }
    if (searchParams.orderType) {
      params = params.set('orderType', searchParams.orderType);
    }
    if (searchParams.serviceId) {
      params = params.set('serviceId', searchParams.serviceId);
    }
    if (searchParams.startDate) {
      params = params.set('startDate', searchParams.startDate);
    }
    if (searchParams.endDate) {
      params = params.set('endDate', searchParams.endDate);
    }
    if (searchParams.minCost !== undefined) {
      params = params.set('minCost', searchParams.minCost.toString());
    }
    if (searchParams.maxCost !== undefined) {
      params = params.set('maxCost', searchParams.maxCost.toString());
    }

    const page = searchParams.page || 1;
    const limit = searchParams.limit || 20;

    return this.getPaginated<OrderResponse>('orders/search', page, limit, params);
  }

  /**
   * Cancel an order
   */
  cancelOrder(orderId: string): Observable<OrderResponse> {
    return this.post<OrderResponse>(`orders/${orderId}/cancel`, {}).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get pending orders
   */
  getPendingOrders(): Observable<OrderResponse[]> {
    return this.get<OrderResponse[]>('orders/pending').pipe(
      map(response => response.data)
    );
  }

  /**
   * Get recent orders
   */
  getRecentOrders(limit: number = 10): Observable<OrderResponse[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.get<OrderResponse[]>('orders/recent', params).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get order statistics
   */
  getOrderStatistics(): Observable<OrderStatistics> {
    return this.get<OrderStatistics>('orders/statistics').pipe(
      map(response => response.data)
    );
  }

  /**
   * Update order
   */
  updateOrder(orderId: string, updates: Partial<CreateOrderRequest>): Observable<OrderResponse> {
    return this.put<OrderResponse>(`orders/${orderId}`, updates).pipe(
      map(response => response.data)
    );
  }
}
