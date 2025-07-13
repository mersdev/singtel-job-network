import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService, ChangePasswordRequest } from '../../../core/api/user.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-2">Settings</h1>
        <p class="text-gray-600">
          Manage your account settings and preferences.
        </p>
      </div>

      <!-- Change Password -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-6 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">Change Password</h3>
          <p class="mt-1 text-sm text-gray-600">
            Update your password to keep your account secure.
          </p>
        </div>

        <form [formGroup]="passwordForm" (ngSubmit)="onChangePassword()" class="p-6">
          <div class="space-y-4">
            <div>
              <label for="currentPassword" class="block text-sm font-medium text-gray-700 mb-1">
                Current Password
              </label>
              <input
                id="currentPassword"
                type="password"
                formControlName="currentPassword"
                class="input-field"
                [class.border-red-500]="passwordForm.get('currentPassword')?.invalid && passwordForm.get('currentPassword')?.touched"
                placeholder="Enter your current password"
              />
              @if (passwordForm.get('currentPassword')?.invalid && passwordForm.get('currentPassword')?.touched) {
                <p class="mt-1 text-sm text-red-600">Current password is required</p>
              }
            </div>

            <div>
              <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">
                New Password
              </label>
              <input
                id="newPassword"
                type="password"
                formControlName="newPassword"
                class="input-field"
                [class.border-red-500]="passwordForm.get('newPassword')?.invalid && passwordForm.get('newPassword')?.touched"
                placeholder="Enter your new password"
              />
              @if (passwordForm.get('newPassword')?.invalid && passwordForm.get('newPassword')?.touched) {
                <p class="mt-1 text-sm text-red-600">
                  @if (passwordForm.get('newPassword')?.errors?.['required']) {
                    New password is required
                  }
                  @if (passwordForm.get('newPassword')?.errors?.['minlength']) {
                    Password must be at least 8 characters long
                  }
                </p>
              }
            </div>

            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
                Confirm New Password
              </label>
              <input
                id="confirmPassword"
                type="password"
                formControlName="confirmPassword"
                class="input-field"
                [class.border-red-500]="passwordForm.get('confirmPassword')?.invalid && passwordForm.get('confirmPassword')?.touched"
                placeholder="Confirm your new password"
              />
              @if (passwordForm.get('confirmPassword')?.invalid && passwordForm.get('confirmPassword')?.touched) {
                <p class="mt-1 text-sm text-red-600">
                  @if (passwordForm.get('confirmPassword')?.errors?.['required']) {
                    Please confirm your new password
                  }
                  @if (passwordForm.get('confirmPassword')?.errors?.['passwordMismatch']) {
                    Passwords do not match
                  }
                </p>
              }
            </div>
          </div>

          <div class="mt-6 flex justify-end">
            <button
              type="submit"
              class="px-4 py-2 text-sm font-medium text-white bg-singtel-blue border border-transparent rounded-md hover:bg-blue-700 disabled:opacity-50"
              [disabled]="passwordForm.invalid || isChangingPassword()"
            >
              @if (isChangingPassword()) {
                <span class="flex items-center">
                  <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Changing Password...
                </span>
              } @else {
                Change Password
              }
            </button>
          </div>
        </form>
      </div>

      <!-- Notification Preferences -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-6 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">Notification Preferences</h3>
          <p class="mt-1 text-sm text-gray-600">
            Choose how you want to receive notifications.
          </p>
        </div>

        <div class="p-6">
          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <div>
                <h4 class="text-sm font-medium text-gray-900">Email Notifications</h4>
                <p class="text-sm text-gray-600">Receive notifications via email</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" [checked]="emailNotifications()" (change)="toggleEmailNotifications()" class="sr-only peer">
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-singtel-blue"></div>
              </label>
            </div>

            <div class="flex items-center justify-between">
              <div>
                <h4 class="text-sm font-medium text-gray-900">Order Updates</h4>
                <p class="text-sm text-gray-600">Get notified about order status changes</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" [checked]="orderNotifications()" (change)="toggleOrderNotifications()" class="sr-only peer">
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-singtel-blue"></div>
              </label>
            </div>

            <div class="flex items-center justify-between">
              <div>
                <h4 class="text-sm font-medium text-gray-900">Service Alerts</h4>
                <p class="text-sm text-gray-600">Receive alerts about service issues</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" [checked]="serviceAlerts()" (change)="toggleServiceAlerts()" class="sr-only peer">
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-singtel-blue"></div>
              </label>
            </div>

            <div class="flex items-center justify-between">
              <div>
                <h4 class="text-sm font-medium text-gray-900">Marketing Communications</h4>
                <p class="text-sm text-gray-600">Receive updates about new services and features</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" [checked]="marketingNotifications()" (change)="toggleMarketingNotifications()" class="sr-only peer">
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-singtel-blue"></div>
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- Account Actions -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-6 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">Account Actions</h3>
          <p class="mt-1 text-sm text-gray-600">
            Manage your account and data.
          </p>
        </div>

        <div class="p-6 space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <h4 class="text-sm font-medium text-gray-900">Download Account Data</h4>
              <p class="text-sm text-gray-600">Export your account data and order history</p>
            </div>
            <button
              type="button"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
              (click)="downloadAccountData()"
            >
              Download
            </button>
          </div>

          <div class="flex items-center justify-between">
            <div>
              <h4 class="text-sm font-medium text-gray-900">Clear Cache</h4>
              <p class="text-sm text-gray-600">Clear stored data to improve performance</p>
            </div>
            <button
              type="button"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
              (click)="clearCache()"
            >
              Clear
            </button>
          </div>
        </div>
      </div>

      <!-- Success/Error Messages -->
      @if (successMessage()) {
        <div class="bg-green-50 border border-green-200 rounded-lg p-4">
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

      @if (errorMessage()) {
        <div class="bg-red-50 border border-red-200 rounded-lg p-4">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <p class="text-sm font-medium text-red-800">{{ errorMessage() }}</p>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class SettingsComponent {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  isChangingPassword = signal(false);
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  // Notification preferences
  emailNotifications = signal(true);
  orderNotifications = signal(true);
  serviceAlerts = signal(true);
  marketingNotifications = signal(false);

  passwordForm: FormGroup = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: this.passwordMatchValidator });

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');
    
    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    return null;
  }

  async onChangePassword(): Promise<void> {
    if (this.passwordForm.invalid) return;

    try {
      this.isChangingPassword.set(true);
      this.errorMessage.set(null);
      this.successMessage.set(null);

      const request: ChangePasswordRequest = {
        currentPassword: this.passwordForm.value.currentPassword,
        newPassword: this.passwordForm.value.newPassword,
        confirmPassword: this.passwordForm.value.confirmPassword
      };

      await this.userService.changePassword(request).toPromise();
      
      this.successMessage.set('Password changed successfully!');
      this.passwordForm.reset();
      
      // Clear success message after 3 seconds
      setTimeout(() => this.successMessage.set(null), 3000);
    } catch (error: any) {
      console.error('Error changing password:', error);
      this.errorMessage.set(error.message || 'Failed to change password');
    } finally {
      this.isChangingPassword.set(false);
    }
  }

  toggleEmailNotifications(): void {
    this.emailNotifications.update(enabled => !enabled);
    this.saveNotificationPreferences();
  }

  toggleOrderNotifications(): void {
    this.orderNotifications.update(enabled => !enabled);
    this.saveNotificationPreferences();
  }

  toggleServiceAlerts(): void {
    this.serviceAlerts.update(enabled => !enabled);
    this.saveNotificationPreferences();
  }

  toggleMarketingNotifications(): void {
    this.marketingNotifications.update(enabled => !enabled);
    this.saveNotificationPreferences();
  }

  private saveNotificationPreferences(): void {
    // Save to localStorage for now (in a real app, this would be saved to the backend)
    const preferences = {
      email: this.emailNotifications(),
      orders: this.orderNotifications(),
      serviceAlerts: this.serviceAlerts(),
      marketing: this.marketingNotifications()
    };
    localStorage.setItem('notificationPreferences', JSON.stringify(preferences));
    
    this.successMessage.set('Notification preferences updated!');
    setTimeout(() => this.successMessage.set(null), 2000);
  }

  downloadAccountData(): void {
    // Placeholder implementation
    this.successMessage.set('Account data download will be available soon!');
    setTimeout(() => this.successMessage.set(null), 3000);
  }

  clearCache(): void {
    // Clear localStorage cache
    const keysToKeep = ['authToken', 'refreshToken', 'notificationPreferences'];
    const allKeys = Object.keys(localStorage);
    
    allKeys.forEach(key => {
      if (!keysToKeep.includes(key)) {
        localStorage.removeItem(key);
      }
    });
    
    this.successMessage.set('Cache cleared successfully!');
    setTimeout(() => this.successMessage.set(null), 2000);
  }
}
