import { Component, inject, signal, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <header class="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-40">
      <div class="flex items-center justify-between h-16 px-4 sm:px-6 lg:px-8">
        <!-- Left side: Logo and menu toggle -->
        <div class="flex items-center">
          <button
            type="button"
            data-cy="mobile-menu-toggle"
            class="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-singtel-blue"
            (click)="toggleSidebar.emit()"
          >
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          
          <div class="flex items-center ml-4 lg:ml-0">
            <img class="h-8 w-auto" src="/assets/images/singtel-logo.svg" alt="Singtel" />
            <span class="ml-3 text-xl font-semibold text-gray-900 hidden sm:block">
              Business Network Portal
            </span>
          </div>
        </div>

        <!-- Center: Search (hidden on mobile) -->
        <div class="hidden md:block flex-1 max-w-lg mx-8">
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <input
              type="text"
              class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-singtel-blue focus:border-singtel-blue"
              placeholder="Search services, orders..."
              [(ngModel)]="searchQuery"
              (input)="onSearch($event)"
            />
          </div>
        </div>

        <!-- Right side: Notifications and user menu -->
        <div class="flex items-center space-x-4">
          <!-- Notifications -->
          <button
            type="button"
            class="p-2 rounded-full text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-singtel-blue relative"
            (click)="toggleNotifications()"
          >
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            @if (unreadNotifications() > 0) {
              <span class="absolute -top-1 -right-1 h-5 w-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                {{ unreadNotifications() }}
              </span>
            }
          </button>

          <!-- User menu -->
          <div class="relative">
            <button
              type="button"
              data-cy="user-menu"
              class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-singtel-blue"
              (click)="toggleUserMenu()"
            >
              @if (currentUser()?.avatar) {
                <img class="h-8 w-8 rounded-full" [src]="currentUser()?.avatar" [alt]="currentUser()?.firstName" />
              } @else {
                <div class="h-8 w-8 rounded-full bg-singtel-blue flex items-center justify-center">
                  <span class="text-sm font-medium text-white">
                    {{ getInitials() }}
                  </span>
                </div>
              }
              <div class="hidden md:block text-left">
                <p class="text-sm font-medium text-gray-900">{{ currentUser()?.firstName }} {{ currentUser()?.lastName }}</p>
                <p class="text-xs text-gray-500">{{ currentUser()?.company }}</p>
              </div>
              <svg class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <!-- User dropdown menu -->
            @if (showUserMenu()) {
              <div class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200">
                <a routerLink="/profile" data-cy="nav-profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                  Your Profile
                </a>
                <a routerLink="/settings" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                  Settings
                </a>
                <div class="border-t border-gray-100"></div>
                <button
                  (click)="logout()"
                  data-cy="logout-button"
                  class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  Sign out
                </button>
              </div>
            }
          </div>
        </div>
      </div>

      <!-- Mobile search -->
      @if (showMobileSearch()) {
        <div class="md:hidden px-4 pb-4">
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <input
              type="text"
              class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-singtel-blue focus:border-singtel-blue"
              placeholder="Search services, orders..."
              [(ngModel)]="searchQuery"
              (input)="onSearch($event)"
            />
          </div>
        </div>
      }
    </header>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class HeaderComponent {
  private authService = inject(AuthService);

  // Signals
  searchQuery = signal('');
  showUserMenu = signal(false);
  showMobileSearch = signal(false);
  unreadNotifications = signal(3);

  // Outputs
  toggleSidebar = output<void>();

  // Computed
  currentUser = this.authService.currentUser;

  getInitials(): string {
    const user = this.currentUser();
    if (!user) return '';
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  toggleUserMenu(): void {
    this.showUserMenu.update(show => !show);
  }

  toggleNotifications(): void {
    // TODO: Implement notifications panel
    console.log('Toggle notifications');
  }

  onSearch(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchQuery.set(target.value);
    // TODO: Implement search functionality
    console.log('Search:', target.value);
  }

  logout(): void {
    this.authService.logout();
  }
}
