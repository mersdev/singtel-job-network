import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
  success: boolean;
  timestamp: string;
}

// Spring Boot Page response structure
export interface SpringPageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: any;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  size: number;
  number: number;
  sort: any;
  empty: boolean;
}

export interface ApiError {
  message: string;
  code: string;
  details?: any;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class BaseApiService {
  protected http = inject(HttpClient);
  protected baseUrl = environment.apiUrl || 'http://localhost:8088/api';

  protected get<T>(endpoint: string, params?: HttpParams): Observable<ApiResponse<T>> {
    return this.http.get<ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, { params }).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  protected post<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    return this.http.post<ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, data).pipe(
      catchError(this.handleError)
    );
  }

  protected put<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    return this.http.put<ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, data).pipe(
      catchError(this.handleError)
    );
  }

  protected patch<T>(endpoint: string, data: any): Observable<ApiResponse<T>> {
    return this.http.patch<ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, data).pipe(
      catchError(this.handleError)
    );
  }

  protected delete<T>(endpoint: string): Observable<ApiResponse<T>> {
    return this.http.delete<ApiResponse<T>>(`${this.baseUrl}/${endpoint}`).pipe(
      catchError(this.handleError)
    );
  }

  protected getPaginated<T>(
    endpoint: string,
    page: number = 1,
    limit: number = 10,
    params?: HttpParams
  ): Observable<PaginatedResponse<T>> {
    const paginationParams = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    let finalParams = paginationParams;
    if (params) {
      finalParams = new HttpParams();
      params.keys().forEach(key => {
        params.getAll(key)?.forEach(value => {
          finalParams = finalParams.append(key, value);
        });
      });
      finalParams = finalParams.append('page', page.toString());
      finalParams = finalParams.append('limit', limit.toString());
    }

    return this.http.get<SpringPageResponse<T> | PaginatedResponse<T>>(`${this.baseUrl}/${endpoint}`, {
      params: finalParams
    }).pipe(
      retry(1),
      map(response => this.convertToPaginatedResponse(response)),
      catchError(this.handleError)
    );
  }

  private convertToPaginatedResponse<T>(response: SpringPageResponse<T> | PaginatedResponse<T>): PaginatedResponse<T> {
    // Check if it's a Spring Boot Page response
    if ('content' in response && 'totalElements' in response) {
      const springResponse = response as SpringPageResponse<T>;
      return {
        data: springResponse.content,
        pagination: {
          page: springResponse.number + 1, // Spring Boot pages are 0-based
          limit: springResponse.size,
          total: springResponse.totalElements,
          totalPages: springResponse.totalPages
        },
        success: true,
        timestamp: new Date().toISOString()
      };
    }

    // Already in the expected format
    return response as PaginatedResponse<T>;
  }

  protected handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = 'An unknown error occurred';
    let errorCode = 'UNKNOWN_ERROR';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
      errorCode = 'CLIENT_ERROR';
    } else {
      // Server-side error
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
        errorCode = error.error.code || `HTTP_${error.status}`;
      } else {
        switch (error.status) {
          case 400:
            errorMessage = 'Bad request. Please check your input.';
            errorCode = 'BAD_REQUEST';
            break;
          case 401:
            errorMessage = 'Unauthorized. Please log in again.';
            errorCode = 'UNAUTHORIZED';
            break;
          case 403:
            errorMessage = 'Forbidden. You do not have permission to access this resource.';
            errorCode = 'FORBIDDEN';
            break;
          case 404:
            errorMessage = 'Resource not found.';
            errorCode = 'NOT_FOUND';
            break;
          case 500:
            errorMessage = 'Internal server error. Please try again later.';
            errorCode = 'INTERNAL_SERVER_ERROR';
            break;
          case 503:
            errorMessage = 'Service unavailable. Please try again later.';
            errorCode = 'SERVICE_UNAVAILABLE';
            break;
          default:
            errorMessage = `Error ${error.status}: ${error.message}`;
            errorCode = `HTTP_${error.status}`;
        }
      }
    }

    const apiError: ApiError = {
      message: errorMessage,
      code: errorCode,
      details: error.error,
      timestamp: new Date().toISOString()
    };

    console.error('API Error:', apiError);
    return throwError(() => apiError);
  };

  protected buildParams(filters: Record<string, any>): HttpParams {
    let params = new HttpParams();
    
    Object.keys(filters).forEach(key => {
      const value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        if (Array.isArray(value)) {
          value.forEach(item => {
            params = params.append(key, item.toString());
          });
        } else {
          params = params.set(key, value.toString());
        }
      }
    });

    return params;
  }
}
