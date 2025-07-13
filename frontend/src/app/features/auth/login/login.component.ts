import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { LoginCredentials } from '../../../core/models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8">
        <!-- Header -->
        <div>
          <div class="mx-auto h-12 w-auto flex justify-center">
            <img class="h-12 w-auto" src="/assets/images/singtel-logo.svg" alt="Singtel" />
          </div>
          <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sign in to your account
          </h2>
          <p class="mt-2 text-center text-sm text-gray-600">
            Access your Singtel Business Network Portal
          </p>
        </div>

        <!-- Error Message -->
        @if (authService.error()) {
          <div data-cy="error-message" class="rounded-md bg-red-50 p-4">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">
                  Login Failed
                </h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>{{ authService.error() }}</p>
                </div>
              </div>
            </div>
          </div>
        }

        <!-- Login Form -->
        <form data-cy="login-form" class="mt-8 space-y-6" [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="rounded-md shadow-sm space-y-4">
            <!-- Username or Email -->
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                Username or Email address
              </label>
              <input
                id="email"
                name="email"
                type="text"
                autocomplete="username"
                required
                formControlName="email"
                data-cy="email-input"
                class="input-field"
                [class.border-red-500]="loginForm.get('email')?.invalid && loginForm.get('email')?.touched"
                placeholder="Enter your username or email"
              />
              @if (loginForm.get('email')?.invalid && loginForm.get('email')?.touched) {
                <p class="mt-1 text-sm text-red-600">
                  @if (loginForm.get('email')?.errors?.['required']) {
                    Username or email is required
                  }
                </p>
              }
            </div>

            <!-- Password -->
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
                Password
              </label>
              <div class="relative">
                <input
                  id="password"
                  name="password"
                  [type]="showPassword() ? 'text' : 'password'"
                  autocomplete="current-password"
                  required
                  formControlName="password"
                  data-cy="password-input"
                  class="input-field pr-10"
                  [class.border-red-500]="loginForm.get('password')?.invalid && loginForm.get('password')?.touched"
                  placeholder="Enter your password"
                />
                <button
                  type="button"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center"
                  (click)="togglePasswordVisibility()"
                >
                  @if (showPassword()) {
                    <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
                    </svg>
                  } @else {
                    <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  }
                </button>
              </div>
              @if (loginForm.get('password')?.invalid && loginForm.get('password')?.touched) {
                <p class="mt-1 text-sm text-red-600">
                  Password is required
                </p>
              }
            </div>
          </div>

          <!-- Remember me and Forgot password -->
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input
                id="remember-me"
                name="remember-me"
                type="checkbox"
                formControlName="rememberMe"
                class="h-4 w-4 text-singtel-blue focus:ring-singtel-blue border-gray-300 rounded"
              />
              <label for="remember-me" class="ml-2 block text-sm text-gray-900">
                Remember me
              </label>
            </div>

            <div class="text-sm">
              <a href="#" class="font-medium text-singtel-blue hover:text-blue-700">
                Forgot your password?
              </a>
            </div>
          </div>

          <!-- Error message -->
          @if (authService.error()) {
            <div class="bg-red-50 border border-red-200 rounded-md p-4">
              <div class="flex">
                <svg class="h-5 w-5 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div class="ml-3">
                  <p class="text-sm text-red-800">{{ authService.error() }}</p>
                </div>
              </div>
            </div>
          }

          <!-- Submit button -->
          <div>
            <button
              type="submit"
              [disabled]="loginForm.invalid || authService.isLoading()"
              data-cy="login-button"
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-singtel-blue hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-singtel-blue disabled:opacity-50 disabled:cursor-not-allowed"
            >
              @if (authService.isLoading()) {
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Signing in...
              } @else {
                Sign in
              }
            </button>
          </div>

          <!-- Demo credentials -->
          <div class="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-md">
            <h4 class="text-sm font-medium text-blue-800 mb-2">Demo Credentials:</h4>
            <p class="text-sm text-blue-700">
              Username: <code class="bg-blue-100 px-1 rounded">john.doe</code><br>
              Password: <code class="bg-blue-100 px-1 rounded">password123</code>
            </p>
            <p class="text-sm text-blue-600 mt-2">
              Or use email: <code class="bg-blue-100 px-1 rounded">john.doe&#64;techstart.sg</code>
            </p>
            <button
              type="button"
              (click)="fillDemoCredentials()"
              class="mt-2 text-xs bg-blue-100 hover:bg-blue-200 text-blue-800 px-2 py-1 rounded transition-colors"
            >
              Fill Demo Credentials
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  
  authService = inject(AuthService);
  showPassword = signal(false);

  loginForm: FormGroup = this.fb.group({
    email: ['john.doe', [Validators.required]],
    password: ['password123', [Validators.required]],
    rememberMe: [false]
  });

  togglePasswordVisibility(): void {
    this.showPassword.update(show => !show);
  }

  fillDemoCredentials(): void {
    this.loginForm.patchValue({
      email: 'john.doe',
      password: 'password123'
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const credentials: LoginCredentials = this.loginForm.value;

      this.authService.login(credentials).subscribe({
        next: (response) => {
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
          this.router.navigate([returnUrl]);
        },
        error: (error) => {
          console.error('Login failed:', error);
        }
      });
    }
  }
}
