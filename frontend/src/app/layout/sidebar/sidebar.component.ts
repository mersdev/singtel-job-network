import { Component, input, signal, computed, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

interface NavigationItem {
  name: string;
  href: string;
  icon: string;
  current?: boolean;
  badge?: number;
  children?: NavigationItem[];
  dataCy?: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside
      data-cy="mobile-nav"
      class="fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 lg:relative lg:translate-x-0 lg:z-auto"
      [class.translate-x-0]="isOpen()"
      [class.-translate-x-full]="!isOpen()"
    >
      <!-- Sidebar header -->
      <div class="flex items-center justify-between h-16 px-6 border-b border-gray-200">
        <div class="flex items-center">
          <img class="h-8 w-auto" src="/assets/images/singtel-logo.svg" alt="Singtel" />
          <span class="ml-3 text-lg font-semibold text-gray-900">Portal</span>
        </div>
        <button
          type="button"
          class="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
          (click)="closeSidebar()"
        >
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
        @for (item of navigation(); track item.name) {
          <div>
            @if (item.children && item.children.length > 0) {
              <!-- Parent item with children -->
              <button
                type="button"
                class="w-full flex items-center justify-between px-3 py-2 text-sm font-medium rounded-lg transition-colors duration-200"
                [class]="getNavItemClasses(item)"
                (click)="toggleSubmenu(item.name)"
              >
                <div class="flex items-center">
                  <i [class]="item.icon + ' h-5 w-5 mr-3'"></i>
                  {{ item.name }}
                </div>
                <svg 
                  class="h-4 w-4 transition-transform duration-200"
                  [class.rotate-90]="expandedMenus().includes(item.name)"
                  fill="none" 
                  viewBox="0 0 24 24" 
                  stroke="currentColor"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>
              
              <!-- Submenu -->
              @if (expandedMenus().includes(item.name)) {
                <div class="ml-8 mt-2 space-y-1">
                  @for (child of item.children; track child.name) {
                    <a
                      [routerLink]="child.href"
                      routerLinkActive="bg-singtel-blue text-white"
                      class="block px-3 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors duration-200"
                    >
                      {{ child.name }}
                    </a>
                  }
                </div>
              }
            } @else {
              <!-- Single navigation item -->
              <a
                [routerLink]="item.href"
                routerLinkActive="bg-singtel-blue text-white"
                [attr.data-cy]="item.dataCy"
                [attr.data-cy-mobile]="getMobileDataCy(item)"
                class="flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-colors duration-200 text-gray-700 hover:text-gray-900 hover:bg-gray-100"
                [class.bg-singtel-blue]="isCurrentRoute(item.href)"
                [class.text-white]="isCurrentRoute(item.href)"
              >
                <i [class]="item.icon + ' h-5 w-5 mr-3'"></i>
                {{ item.name }}
                @if (item.badge && item.badge > 0) {
                  <span class="ml-auto bg-red-500 text-white text-xs px-2 py-1 rounded-full">
                    {{ item.badge }}
                  </span>
                }
              </a>
            }
          </div>
        }
      </nav>

      <!-- Sidebar footer -->
      <div class="border-t border-gray-200 p-4">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="h-8 w-8 rounded-full bg-singtel-blue flex items-center justify-center">
              <span class="text-sm font-medium text-white">JD</span>
            </div>
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-900">John Doe</p>
            <p class="text-xs text-gray-500">Administrator</p>
          </div>
        </div>
      </div>
    </aside>

    <!-- Overlay for mobile -->
    @if (isOpen()) {
      <div 
        class="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
        (click)="closeSidebar()"
      ></div>
    }
  `,
  styles: [`
    :host {
      display: contents;
    }
  `]
})
export class SidebarComponent {
  isOpen = input<boolean>(false);

  // Output for closing sidebar
  sidebarClose = output<void>();

  private expandedMenusSignal = signal<string[]>([]);
  expandedMenus = this.expandedMenusSignal.asReadonly();

  navigation = signal<NavigationItem[]>([
    {
      name: 'Dashboard',
      href: '/dashboard',
      icon: 'fas fa-tachometer-alt',
      dataCy: 'nav-dashboard'
    },
    {
      name: 'Services',
      href: '/services',
      icon: 'fas fa-server',
      dataCy: 'nav-services'
    },
    {
      name: 'Orders',
      href: '/orders',
      icon: 'fas fa-shopping-cart',
      dataCy: 'nav-orders'
    },
    {
      name: 'Provisioning',
      href: '/provisioning',
      icon: 'fas fa-cogs',
      children: [
        { name: 'New Order', href: '/provisioning/new', icon: 'fas fa-plus' },
        { name: 'Order History', href: '/provisioning/history', icon: 'fas fa-history' },
        { name: 'Templates', href: '/provisioning/templates', icon: 'fas fa-file-alt' }
      ]
    },
    {
      name: 'Bandwidth',
      href: '/bandwidth',
      icon: 'fas fa-chart-line',
      dataCy: 'nav-bandwidth'
    },
    {
      name: 'Monitoring',
      href: '/monitoring',
      icon: 'fas fa-chart-bar',
      dataCy: 'nav-monitoring',
      children: [
        { name: 'Service Status', href: '/monitoring/status', icon: 'fas fa-heartbeat' },
        { name: 'Performance', href: '/monitoring/performance', icon: 'fas fa-tachometer-alt' },
        { name: 'Alerts', href: '/monitoring/alerts', icon: 'fas fa-exclamation-triangle', badge: 3 }
      ]
    },
    {
      name: 'Reports',
      href: '/reports',
      icon: 'fas fa-file-alt',
      children: [
        { name: 'Usage Reports', href: '/reports/usage', icon: 'fas fa-chart-pie' },
        { name: 'Billing Reports', href: '/reports/billing', icon: 'fas fa-dollar-sign' },
        { name: 'Performance Reports', href: '/reports/performance', icon: 'fas fa-chart-line' }
      ]
    },
    {
      name: 'Settings',
      href: '/settings',
      icon: 'fas fa-cog',
      dataCy: 'nav-settings'
    }
  ]);

  toggleSubmenu(menuName: string): void {
    this.expandedMenusSignal.update(menus => {
      const index = menus.indexOf(menuName);
      if (index > -1) {
        return menus.filter(m => m !== menuName);
      } else {
        return [...menus, menuName];
      }
    });
  }

  closeSidebar(): void {
    this.sidebarClose.emit();
  }

  isCurrentRoute(href: string): boolean {
    // This would check against current router state
    return false; // Placeholder
  }

  getNavItemClasses(item: NavigationItem): string {
    const baseClasses = 'text-gray-700 hover:text-gray-900 hover:bg-gray-100';
    const activeClasses = 'bg-singtel-blue text-white';

    return this.isCurrentRoute(item.href) ? activeClasses : baseClasses;
  }

  getMobileDataCy(item: NavigationItem): string | null {
    if (item.href === '/services') return 'mobile-nav-services';
    if (item.href === '/orders') return 'mobile-nav-orders';
    return null;
  }
}
