import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map, retry, catchError } from 'rxjs/operators';
import { HttpParams, HttpClient } from '@angular/common/http';
import { BaseApiService, ApiResponse, PaginatedResponse } from './base-api.service';
import { environment } from '../../../environments/environment';

export interface ServiceCategory {
  id: string;
  name: string;
  description: string;
  iconUrl?: string;
  isActive: boolean;
  serviceCount: number;
}

export interface ServiceSummary {
  id: string;
  name: string;
  description: string;
  categoryId?: string;
  categoryName: string;
  serviceType: string;
  baseBandwidthMbps: number;
  maxBandwidthMbps: number;
  minBandwidthMbps: number;
  basePriceMonthly: number;
  monthlyPrice?: number; // For backward compatibility
  pricePerMbps?: number;
  setupFee: number;
  contractTermMonths: number;
  isBandwidthAdjustable: boolean;
  isAvailable: boolean;
  provisioningTimeHours: number;
}

export interface ServiceDetail extends ServiceSummary {
  features: Record<string, any>;
  technicalSpecs: Record<string, any>;
  supportedBandwidths: number[];
  availableLocations: string[];
}

export interface ServiceSearchParams {
  name?: string;
  categoryId?: string;
  serviceType?: string;
  minPrice?: number;
  maxPrice?: number;
  minBandwidth?: number;
  maxBandwidth?: number;
  bandwidthAdjustable?: boolean;
  page?: number;
  limit?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ServiceCatalogService extends BaseApiService {
  protected override http = inject(HttpClient);
  protected override baseUrl = environment.apiUrl || 'http://localhost:8088/api';

  /**
   * Get all service categories
   */
  getCategories(): Observable<ServiceCategory[]> {
    return this.http.get<ServiceCategory[]>(`${this.baseUrl}/services/categories`).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  /**
   * Get category by ID
   */
  getCategoryById(categoryId: string): Observable<ServiceCategory> {
    return this.http.get<ServiceCategory>(`${this.baseUrl}/services/categories/${categoryId}`).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  /**
   * Get all services
   */
  getAllServices(): Observable<ServiceSummary[]> {
    return this.http.get<ServiceSummary[]>(`${this.baseUrl}/services`).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  /**
   * Get services with pagination
   */
  getServicesPaged(page: number = 1, limit: number = 20): Observable<PaginatedResponse<ServiceSummary>> {
    return this.getPaginated<ServiceSummary>('services/paged', page, limit);
  }

  /**
   * Get service by ID
   */
  getServiceById(serviceId: string): Observable<ServiceDetail> {
    return this.http.get<ServiceDetail>(`${this.baseUrl}/services/${serviceId}`).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  /**
   * Search services
   */
  searchServices(searchParams: ServiceSearchParams): Observable<PaginatedResponse<ServiceSummary>> {
    let params = new HttpParams();

    if (searchParams.name) {
      params = params.set('name', searchParams.name);
    }
    if (searchParams.categoryId) {
      params = params.set('categoryId', searchParams.categoryId);
    }
    if (searchParams.serviceType) {
      params = params.set('serviceType', searchParams.serviceType);
    }
    if (searchParams.minPrice !== undefined) {
      params = params.set('minPrice', searchParams.minPrice.toString());
    }
    if (searchParams.maxPrice !== undefined) {
      params = params.set('maxPrice', searchParams.maxPrice.toString());
    }
    if (searchParams.minBandwidth !== undefined) {
      params = params.set('minBandwidth', searchParams.minBandwidth.toString());
    }
    if (searchParams.maxBandwidth !== undefined) {
      params = params.set('maxBandwidth', searchParams.maxBandwidth.toString());
    }
    if (searchParams.bandwidthAdjustable !== undefined) {
      params = params.set('bandwidthAdjustable', searchParams.bandwidthAdjustable.toString());
    }

    const page = searchParams.page || 1;
    const limit = searchParams.limit || 20;
    params = params.set('page', page.toString()).set('limit', limit.toString());

    // For now, return all services and filter client-side since backend search might not be implemented
    return this.getAllServices().pipe(
      map(services => {
        let filteredServices = services;

        // Apply filters
        if (searchParams.name) {
          filteredServices = filteredServices.filter(service =>
            service.name.toLowerCase().includes(searchParams.name!.toLowerCase())
          );
        }
        if (searchParams.categoryId) {
          // Map category ID to category name for filtering
          const categoryMap: { [key: string]: string } = {
            '550e8400-e29b-41d4-a716-446655440001': 'Business Internet',
            '550e8400-e29b-41d4-a716-446655440002': 'VPN Services',
            '550e8400-e29b-41d4-a716-446655440003': 'Dedicated Lines',
            '550e8400-e29b-41d4-a716-446655440004': 'Cloud Connect'
          };
          const categoryName = categoryMap[searchParams.categoryId];
          if (categoryName) {
            filteredServices = filteredServices.filter(service =>
              service.categoryName === categoryName
            );
          }
        }
        if (searchParams.serviceType) {
          filteredServices = filteredServices.filter(service =>
            service.serviceType === searchParams.serviceType
          );
        }
        if (searchParams.minPrice !== undefined) {
          filteredServices = filteredServices.filter(service =>
            service.basePriceMonthly >= searchParams.minPrice!
          );
        }
        if (searchParams.maxPrice !== undefined) {
          filteredServices = filteredServices.filter(service =>
            service.basePriceMonthly <= searchParams.maxPrice!
          );
        }

        // Apply pagination
        const startIndex = (page - 1) * limit;
        const endIndex = startIndex + limit;
        const paginatedServices = filteredServices.slice(startIndex, endIndex);

        return {
          data: paginatedServices,
          pagination: {
            page: page,
            limit: limit,
            total: filteredServices.length,
            totalPages: Math.ceil(filteredServices.length / limit)
          },
          success: true,
          timestamp: new Date().toISOString()
        };
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get services by category
   */
  getServicesByCategory(categoryId: string, page: number = 1, limit: number = 20): Observable<PaginatedResponse<ServiceSummary>> {
    const params = new HttpParams().set('categoryId', categoryId);
    return this.getPaginated<ServiceSummary>('services/search', page, limit, params);
  }

  /**
   * Get bandwidth adjustable services
   */
  getBandwidthAdjustableServices(): Observable<ServiceSummary[]> {
    return this.getAllServices().pipe(
      map(services => services.filter(service => service.isBandwidthAdjustable))
    );
  }

  /**
   * Get service types
   */
  getServiceTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/services/types`).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  /**
   * Get popular services
   */
  getPopularServices(limit: number = 10): Observable<ServiceSummary[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.get<ServiceSummary[]>('services/popular', params).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get featured services
   */
  getFeaturedServices(): Observable<ServiceSummary[]> {
    return this.get<ServiceSummary[]>('services/featured').pipe(
      map(response => response.data)
    );
  }
}
