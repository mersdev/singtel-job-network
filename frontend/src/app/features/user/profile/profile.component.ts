import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, UserProfile, UpdateUserProfileRequest } from '../../../core/api/user.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 data-cy="profile-header" class="text-2xl font-bold text-gray-900 mb-2">User Profile</h1>
        <p class="text-gray-600">
          Manage your personal information and account settings.
        </p>
      </div>

      <!-- Loading State -->
      @if (isLoading()) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <div data-cy="loading-spinner" class="animate-spin rounded-full h-12 w-12 border-b-2 border-singtel-blue mx-auto"></div>
          <p class="mt-4 text-gray-600">Loading profile...</p>
        </div>
      }

      <!-- Error State -->
      @if (error()) {
        <div data-cy="error-message" class="bg-red-50 border border-red-200 rounded-lg p-6">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <h3 class="text-sm font-medium text-red-800">Error loading profile</h3>
              <p class="mt-1 text-sm text-red-700">{{ error() }}</p>
              <button
                (click)="loadProfile()"
                class="mt-2 text-sm font-medium text-red-800 hover:text-red-900 underline"
              >
                Try again
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Profile Form -->
      @if (!isLoading() && !error() && profile()) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
          <form data-cy="profile-form" [formGroup]="profileForm" (ngSubmit)="onSubmit()">
            <!-- Personal Information -->
            <div class="p-6 border-b border-gray-200">
              <h3 class="text-lg font-medium text-gray-900 mb-4">Personal Information</h3>
              
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label for="firstName" class="block text-sm font-medium text-gray-700 mb-1">
                    First Name
                  </label>
                  <input
                    id="firstName"
                    type="text"
                    formControlName="firstName"
                    data-cy="first-name-input"
                    class="input-field"
                    [class.border-red-500]="profileForm.get('firstName')?.invalid && profileForm.get('firstName')?.touched"
                  />
                  @if (profileForm.get('firstName')?.invalid && profileForm.get('firstName')?.touched) {
                    <p data-cy="first-name-error" class="mt-1 text-sm text-red-600">First name is required</p>
                  }
                </div>

                <div>
                  <label for="lastName" class="block text-sm font-medium text-gray-700 mb-1">
                    Last Name
                  </label>
                  <input
                    id="lastName"
                    type="text"
                    formControlName="lastName"
                    data-cy="last-name-input"
                    class="input-field"
                    [class.border-red-500]="profileForm.get('lastName')?.invalid && profileForm.get('lastName')?.touched"
                  />
                  @if (profileForm.get('lastName')?.invalid && profileForm.get('lastName')?.touched) {
                    <p data-cy="last-name-error" class="mt-1 text-sm text-red-600">Last name is required</p>
                  }
                </div>

                <div>
                  <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    Email Address
                  </label>
                  <input
                    id="email"
                    type="email"
                    formControlName="email"
                    data-cy="email-input"
                    class="input-field"
                    [class.border-red-500]="profileForm.get('email')?.invalid && profileForm.get('email')?.touched"
                  />
                  @if (profileForm.get('email')?.invalid && profileForm.get('email')?.touched) {
                    <p data-cy="email-error" class="mt-1 text-sm text-red-600">
                      @if (profileForm.get('email')?.errors?.['required']) {
                        Email is required
                      }
                      @if (profileForm.get('email')?.errors?.['email']) {
                        Please enter a valid email address
                      }
                    </p>
                  }
                </div>

                <div>
                  <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">
                    Phone Number
                  </label>
                  <input
                    id="phone"
                    type="tel"
                    formControlName="phone"
                    data-cy="phone-input"
                    class="input-field"
                    placeholder="+65 1234 5678"
                  />
                </div>
              </div>
            </div>

            <!-- Account Information (Read-only) -->
            <div class="p-6 border-b border-gray-200">
              <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg font-medium text-gray-900">Account Information</h3>
                <button
                  type="button"
                  data-cy="reload-profile"
                  (click)="loadProfile()"
                  class="px-3 py-1 text-sm text-gray-600 hover:text-gray-800 border border-gray-300 rounded-md hover:bg-gray-50"
                >
                  Reload
                </button>
              </div>
              
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Username</label>
                  <div data-cy="username-display" class="input-field bg-gray-50 text-gray-500">{{ profile()?.username }}</div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Role</label>
                  <div data-cy="role-display" class="input-field bg-gray-50 text-gray-500">{{ profile()?.role }}</div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
                  <span data-cy="status-display" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        [class]="getStatusColor(profile()?.status || '')">
                    {{ profile()?.status }}
                  </span>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Last Login</label>
                  <div class="input-field bg-gray-50 text-gray-500">
                    {{ profile()?.lastLoginAt ? formatDate(profile()?.lastLoginAt || '') : 'Never' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Company Information (Read-only) -->
            @if (profile()?.company) {
              <div class="p-6 border-b border-gray-200">
                <h3 class="text-lg font-medium text-gray-900 mb-4">Company Information</h3>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Company Name</label>
                    <div class="input-field bg-gray-50 text-gray-500">{{ profile()?.company?.name }}</div>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Registration Number</label>
                    <div class="input-field bg-gray-50 text-gray-500">{{ profile()?.company?.registrationNumber }}</div>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Industry</label>
                    <div class="input-field bg-gray-50 text-gray-500">{{ profile()?.company?.industry }}</div>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Contact Email</label>
                    <div class="input-field bg-gray-50 text-gray-500">{{ profile()?.company?.email }}</div>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Contact Phone</label>
                    <div class="input-field bg-gray-50 text-gray-500">{{ profile()?.company?.phone }}</div>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Address</label>
                    <div class="input-field bg-gray-50 text-gray-500">{{ profile()?.company?.address }}</div>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Country</label>
                    <div class="input-field bg-gray-50 text-gray-500">{{ profile()?.company?.country }}</div>
                  </div>
                </div>
              </div>
            }

            <!-- Form Actions -->
            <div class="px-6 py-4 flex justify-between">
              <button
                type="button"
                (click)="resetForm()"
                data-cy="reset-button"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                [disabled]="isSaving()"
              >
                Reset
              </button>

              <button
                type="submit"
                data-cy="save-button"
                class="px-4 py-2 text-sm font-medium text-white bg-singtel-blue border border-transparent rounded-md hover:bg-blue-700 disabled:opacity-50"
                [disabled]="profileForm.invalid || isSaving()"
              >
                @if (isSaving()) {
                  <span class="flex items-center">
                    <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Saving...
                  </span>
                } @else {
                  Save Changes
                }
              </button>
            </div>
          </form>
        </div>
      }

      <!-- Success Message -->
      @if (successMessage()) {
        <div data-cy="success-message" class="bg-green-50 border border-green-200 rounded-lg p-4">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <p class="text-sm font-medium text-green-800">{{ successMessage() }}</p>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  profile = signal<UserProfile | null>(null);
  isLoading = signal(false);
  isSaving = signal(false);
  error = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  profileForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['']
  });

  ngOnInit(): void {
    // Check if user is authenticated before loading profile
    if (!this.authService.isAuthenticated()) {
      this.error.set('You must be logged in to view your profile.');
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: '/profile' }
      });
      return;
    }

    this.loadProfile();
  }

  async loadProfile(): Promise<void> {
    try {
      this.isLoading.set(true);
      this.error.set(null);

      const profile = await this.userService.getCurrentUserProfile().toPromise();
      if (profile) {
        this.profile.set(profile);
        this.profileForm.patchValue({
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email,
          phone: profile.phone || ''
        });
      }
    } catch (error: any) {
      console.error('Error loading profile:', error);

      // Handle authentication errors specifically
      if (error.status === 401 || error.code === 'UNAUTHORIZED') {
        this.error.set('Your session has expired. Please log in again.');
        this.authService.logout();
        return;
      }

      this.error.set(error.message || 'Failed to load profile');
    } finally {
      this.isLoading.set(false);
    }
  }

  async onSubmit(): Promise<void> {
    if (this.profileForm.invalid) return;

    try {
      this.isSaving.set(true);
      this.error.set(null);
      this.successMessage.set(null);

      const updates: UpdateUserProfileRequest = {
        firstName: this.profileForm.value.firstName,
        lastName: this.profileForm.value.lastName,
        email: this.profileForm.value.email,
        phone: this.profileForm.value.phone || undefined
      };

      const updatedProfile = await this.userService.updateProfile(updates).toPromise();
      if (updatedProfile) {
        this.profile.set(updatedProfile);
        this.successMessage.set('Profile updated successfully!');
        
        // Clear success message after 3 seconds
        setTimeout(() => this.successMessage.set(null), 3000);
      }
    } catch (error: any) {
      console.error('Error updating profile:', error);

      // Handle authentication errors specifically
      if (error.status === 401 || error.code === 'UNAUTHORIZED') {
        this.error.set('Your session has expired. Please log in again.');
        this.authService.logout();
        return;
      }

      this.error.set(error.message || 'Failed to update profile');
    } finally {
      this.isSaving.set(false);
    }
  }

  resetForm(): void {
    const profile = this.profile();
    if (profile) {
      this.profileForm.patchValue({
        firstName: profile.firstName,
        lastName: profile.lastName,
        email: profile.email,
        phone: profile.phone || ''
      });
    }
    this.error.set(null);
    this.successMessage.set(null);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'INACTIVE':
        return 'bg-red-100 text-red-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    return new Date(dateString).toLocaleString();
  }
}
