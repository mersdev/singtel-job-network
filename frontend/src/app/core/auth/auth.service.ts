import { Injectable, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { User, LoginCredentials, AuthResponse } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  private readonly TOKEN_KEY = 'singtel_auth_token';
  private readonly REFRESH_TOKEN_KEY = 'singtel_refresh_token';
  private readonly USER_KEY = 'singtel_user';

  private get isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  // Signals for reactive state management
  private currentUserSignal = signal<User | null>(this.getUserFromStorage());
  private isLoadingSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);

  // Public readonly signals
  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isLoading = this.isLoadingSignal.asReadonly();
  readonly error = this.errorSignal.asReadonly();

  constructor() {
    this.initializeAuth();
  }

  private initializeAuth(): void {
    const token = this.getToken();
    if (token && this.isTokenValid(token)) {
      // Token exists and is valid, user is authenticated
      const user = this.getUserFromStorage();
      if (user) {
        this.currentUserSignal.set(user);
      }
    } else {
      // Token is invalid or doesn't exist, clear storage
      this.clearAuthData();
    }
  }

  login(credentials: LoginCredentials): Observable<AuthResponse> {
    this.isLoadingSignal.set(true);
    this.errorSignal.set(null);

    // Call real backend API
    const loginRequest = {
      usernameOrEmail: credentials.email,
      password: credentials.password,
      rememberMe: credentials.rememberMe || false
    };

    return this.http.post<any>(`${environment.apiUrl}/auth/login`, loginRequest).pipe(
      map(response => {
        // Transform backend response to frontend format
        const authResponse: AuthResponse = {
          user: {
            id: response.user.id,
            email: response.user.email,
            firstName: response.user.firstName,
            lastName: response.user.lastName,
            company: response.user.company?.name || '',
            role: response.user.role.toLowerCase() as any,
            preferences: {
              theme: 'light',
              notifications: {
                email: true,
                push: true,
                sms: false
              },
              dashboard: {
                defaultView: 'overview',
                refreshInterval: 30000
              }
            }
          },
          token: response.accessToken,
          refreshToken: response.refreshToken,
          expiresIn: response.expiresIn
        };
        return authResponse;
      }),
      tap(response => {
        this.handleAuthSuccess(response);
      }),
      catchError(error => {
        const errorMessage = error.error?.message || error.message || 'Login failed';
        this.errorSignal.set(errorMessage);
        this.isLoadingSignal.set(false);
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    this.clearAuthData();
    this.currentUserSignal.set(null);
    this.router.navigate(['/auth/login']);
  }

  refreshToken(): Observable<AuthResponse> {
    if (!this.isBrowser) {
      return throwError(() => new Error('Not in browser environment'));
    }

    const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<any>(`${environment.apiUrl}/auth/refresh`, { refreshToken }).pipe(
      map(response => {
        // Transform backend response to frontend format
        const authResponse: AuthResponse = {
          user: {
            id: response.userProfile.id,
            email: response.userProfile.email,
            firstName: response.userProfile.firstName,
            lastName: response.userProfile.lastName,
            company: response.userProfile.companyName,
            role: response.userProfile.role.toLowerCase() as any,
            preferences: {
              theme: 'light',
              notifications: {
                email: true,
                push: true,
                sms: false
              },
              dashboard: {
                defaultView: 'overview',
                refreshInterval: 30000
              }
            }
          },
          token: response.accessToken,
          refreshToken: response.refreshToken,
          expiresIn: response.expiresIn
        };
        return authResponse;
      }),
      tap(response => {
        this.handleAuthSuccess(response);
      }),
      catchError(error => {
        this.logout();
        return throwError(() => error);
      })
    );
  }

  private handleAuthSuccess(response: AuthResponse): void {
    if (this.isBrowser) {
      localStorage.setItem(this.TOKEN_KEY, response.token);
      localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
      localStorage.setItem(this.USER_KEY, JSON.stringify(response.user));
    }

    this.currentUserSignal.set(response.user);
    this.isLoadingSignal.set(false);
    this.errorSignal.set(null);
  }

  private clearAuthData(): void {
    if (this.isBrowser) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.REFRESH_TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
  }

  private getUserFromStorage(): User | null {
    if (!this.isBrowser) {
      return null;
    }

    try {
      const userJson = localStorage.getItem(this.USER_KEY);
      return userJson ? JSON.parse(userJson) : null;
    } catch {
      return null;
    }
  }

  getToken(): string | null {
    if (!this.isBrowser) {
      return null;
    }
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private isTokenValid(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp > currentTime;
    } catch {
      return false;
    }
  }

  isAuthenticated(): boolean {
    return !!this.currentUser();
  }


}
