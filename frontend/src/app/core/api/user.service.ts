import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseApiService, ApiResponse } from './base-api.service';

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: UserRole;
  status: UserStatus;
  lastLoginAt?: string;
  company?: {
    id: string;
    name: string;
    registrationNumber: string;
    email: string;
    phone: string;
    address: string;
    postalCode: string;
    country: string;
    industry: string;
    companySize: string;
    status: string;
  };
}

export interface UpdateUserProfileRequest {
  firstName?: string;
  lastName?: string;
  phone?: string;
  email?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  MANAGER = 'MANAGER',
  USER = 'USER',
  VIEWER = 'VIEWER'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  PENDING_VERIFICATION = 'PENDING_VERIFICATION'
}

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseApiService {
  protected override http = inject(HttpClient);

  /**
   * Get current user profile
   */
  getCurrentUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.baseUrl}/auth/me`);
  }

  /**
   * Update current user profile
   */
  updateProfile(updates: UpdateUserProfileRequest): Observable<UserProfile> {
    return this.put<UserProfile>('auth/profile', updates).pipe(
      map(response => response.data)
    );
  }

  /**
   * Change password
   */
  changePassword(request: ChangePasswordRequest): Observable<{ message: string }> {
    return this.post<{ message: string }>('auth/change-password', request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get user by ID (admin only)
   */
  getUserById(userId: string): Observable<UserProfile> {
    return this.get<UserProfile>(`users/${userId}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get all users in company (admin/manager only)
   */
  getCompanyUsers(): Observable<UserProfile[]> {
    return this.get<UserProfile[]>('users/company').pipe(
      map(response => response.data)
    );
  }

  /**
   * Create new user (admin only)
   */
  createUser(userData: {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string;
    role: UserRole;
    password: string;
  }): Observable<UserProfile> {
    return this.post<UserProfile>('users', userData).pipe(
      map(response => response.data)
    );
  }

  /**
   * Update user (admin only)
   */
  updateUser(userId: string, updates: {
    firstName?: string;
    lastName?: string;
    phoneNumber?: string;
    email?: string;
    role?: UserRole;
    status?: UserStatus;
  }): Observable<UserProfile> {
    return this.put<UserProfile>(`users/${userId}`, updates).pipe(
      map(response => response.data)
    );
  }

  /**
   * Delete user (admin only)
   */
  deleteUser(userId: string): Observable<{ message: string }> {
    return this.delete<{ message: string }>(`users/${userId}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Reset user password (admin only)
   */
  resetUserPassword(userId: string): Observable<{ message: string; temporaryPassword: string }> {
    return this.post<{ message: string; temporaryPassword: string }>(`users/${userId}/reset-password`, {}).pipe(
      map(response => response.data)
    );
  }

  /**
   * Activate user (admin only)
   */
  activateUser(userId: string): Observable<UserProfile> {
    return this.post<UserProfile>(`users/${userId}/activate`, {}).pipe(
      map(response => response.data)
    );
  }

  /**
   * Deactivate user (admin only)
   */
  deactivateUser(userId: string): Observable<UserProfile> {
    return this.post<UserProfile>(`users/${userId}/deactivate`, {}).pipe(
      map(response => response.data)
    );
  }

  /**
   * Get user activity log (admin only)
   */
  getUserActivityLog(userId: string): Observable<any[]> {
    return this.get<any[]>(`users/${userId}/activity`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Upload user avatar
   */
  uploadAvatar(file: File): Observable<{ avatarUrl: string }> {
    const formData = new FormData();
    formData.append('avatar', file);
    
    return this.http.post<ApiResponse<{ avatarUrl: string }>>(`${this.baseUrl}/auth/avatar`, formData).pipe(
      map(response => response.data)
    );
  }

  /**
   * Delete user avatar
   */
  deleteAvatar(): Observable<{ message: string }> {
    return this.delete<{ message: string }>('auth/avatar').pipe(
      map(response => response.data)
    );
  }
}
