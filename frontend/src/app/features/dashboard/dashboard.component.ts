import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { OrderService, OrderResponse, OrderStatistics } from '../../core/api/order.service';
import { ServiceCatalogService, ServiceSummary } from '../../core/api/service-catalog.service';

interface DashboardCard {
  title: string;
  value: string | number;
  change: string;
  changeType: 'increase' | 'decrease' | 'neutral';
  icon: string;
  color: string;
}

interface QuickAction {
  title: string;
  description: string;
  icon: string;
  route: string;
  color: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="space-y-6">
      <!-- Welcome Header -->
      <div class="bg-gradient-to-r from-singtel-blue to-blue-700 rounded-lg shadow-sm p-6 text-white">
        <h1 data-cy="dashboard-header" class="text-2xl font-bold">
          Welcome back, {{ currentUser()?.firstName }}!
        </h1>
        <p class="mt-2 text-blue-100">
          Here's what's happening with your network services today.
        </p>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        @for (card of dashboardCards(); track card.title) {
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <div [class]="'w-8 h-8 rounded-md flex items-center justify-center ' + card.color">
                  <i [class]="card.icon + ' text-white'"></i>
                </div>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">{{ card.title }}</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ card.value }}</div>
                    <div 
                      class="ml-2 flex items-baseline text-sm font-semibold"
                      [class.text-green-600]="card.changeType === 'increase'"
                      [class.text-red-600]="card.changeType === 'decrease'"
                      [class.text-gray-500]="card.changeType === 'neutral'"
                    >
                      @if (card.changeType === 'increase') {
                        <svg class="self-center flex-shrink-0 h-4 w-4 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                        </svg>
                      }
                      @if (card.changeType === 'decrease') {
                        <svg class="self-center flex-shrink-0 h-4 w-4 text-red-500" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M14.707 10.293a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 111.414-1.414L9 12.586V5a1 1 0 012 0v7.586l2.293-2.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                        </svg>
                      }
                      <span class="sr-only">
                        {{ card.changeType === 'increase' ? 'Increased' : card.changeType === 'decrease' ? 'Decreased' : 'No change' }} by
                      </span>
                      {{ card.change }}
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        }
      </div>

      <!-- Quick Actions -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-4">Quick Actions</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          @for (action of quickActions(); track action.title) {
            <a
              [routerLink]="action.route"
              class="group relative rounded-lg p-6 bg-white hover:bg-gray-50 border border-gray-200 hover:border-gray-300 transition-all duration-200"
            >
              <div>
                <span [class]="'rounded-lg inline-flex p-3 ring-4 ring-white ' + action.color">
                  <i [class]="action.icon + ' h-6 w-6 text-white'"></i>
                </span>
              </div>
              <div class="mt-4">
                <h3 class="text-lg font-medium text-gray-900 group-hover:text-gray-700">
                  {{ action.title }}
                </h3>
                <p class="mt-2 text-sm text-gray-500">
                  {{ action.description }}
                </p>
              </div>
              <span class="pointer-events-none absolute top-6 right-6 text-gray-300 group-hover:text-gray-400" aria-hidden="true">
                <svg class="h-6 w-6" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M20 4h1a1 1 0 00-1-1v1zm-1 12a1 1 0 102 0h-2zM8 3a1 1 0 000 2V3zM3.293 19.293a1 1 0 101.414 1.414l-1.414-1.414zM19 4v12h2V4h-2zm1-1H8v2h12V3zm-.707.293l-16 16 1.414 1.414 16-16-1.414-1.414z" />
                </svg>
              </span>
            </a>
          }
      </div>

      <!-- Recent Activity -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Popular Services -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-medium text-gray-900">Popular Services</h2>
            @if (isLoadingServices()) {
              <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-singtel-blue"></div>
            }
          </div>

          @if (servicesError()) {
            <div class="text-center py-4">
              <p class="text-sm text-red-600">{{ servicesError() }}</p>
              <button
                (click)="loadPopularServices()"
                class="mt-2 text-sm text-singtel-blue hover:text-blue-700 underline"
              >
                Try again
              </button>
            </div>
          } @else if (recentServices().length === 0 && !isLoadingServices()) {
            <div class="text-center py-8">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
              <p class="mt-2 text-sm text-gray-500">No services available</p>
            </div>
          } @else {
            <div class="space-y-4">
              @for (service of recentServices(); track service.id) {
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <div
                      class="w-3 h-3 rounded-full mr-3"
                      [class.bg-green-500]="service.isAvailable"
                      [class.bg-red-500]="!service.isAvailable"
                    ></div>
                    <div>
                      <p class="text-sm font-medium text-gray-900">{{ service.name }}</p>
                      <p class="text-xs text-gray-500">{{ service.categoryName }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-medium text-gray-900">\${{ service.monthlyPrice }}/month</p>
                    <span
                      class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium"
                      [class.bg-green-100]="service.isAvailable"
                      [class.text-green-800]="service.isAvailable"
                      [class.bg-red-100]="!service.isAvailable"
                      [class.text-red-800]="!service.isAvailable"
                    >
                      {{ service.isAvailable ? 'Available' : 'Unavailable' }}
                    </span>
                  </div>
                </div>
              }
            </div>
          }
        </div>

        <!-- Recent Orders -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-medium text-gray-900">Recent Orders</h2>
            @if (isLoadingOrders()) {
              <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-singtel-blue"></div>
            }
          </div>

          @if (ordersError()) {
            <div class="text-center py-4">
              <p class="text-sm text-red-600">{{ ordersError() }}</p>
              <button
                (click)="loadRecentOrders()"
                class="mt-2 text-sm text-singtel-blue hover:text-blue-700 underline"
              >
                Try again
              </button>
            </div>
          } @else if (recentOrders().length === 0 && !isLoadingOrders()) {
            <div class="text-center py-8">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p class="mt-2 text-sm text-gray-500">No recent orders</p>
            </div>
          } @else {
            <div class="space-y-4">
              @for (order of recentOrders(); track order.id) {
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm font-medium text-gray-900">{{ order.service.name }}</p>
                    <p class="text-xs text-gray-500">{{ getRelativeTime(order.createdAt) }}</p>
                    <p class="text-xs text-gray-400">Order #{{ order.orderNumber }}</p>
                  </div>
                  <span
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    [class]="getStatusColor(order.status)"
                  >
                    {{ order.status.replace('_', ' ') | titlecase }}
                  </span>
                </div>
              }
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private orderService = inject(OrderService);
  private serviceCatalogService = inject(ServiceCatalogService);

  currentUser = this.authService.currentUser;

  // Loading states
  isLoadingStats = signal(false);
  isLoadingOrders = signal(false);
  isLoadingServices = signal(false);

  // Error states
  statsError = signal<string | null>(null);
  ordersError = signal<string | null>(null);
  servicesError = signal<string | null>(null);

  dashboardCards = signal<DashboardCard[]>([
    {
      title: 'Active Services',
      value: 12,
      change: '+2.5%',
      changeType: 'increase',
      icon: 'fas fa-server',
      color: 'bg-blue-500'
    },
    {
      title: 'Total Bandwidth',
      value: '2.4 Gbps',
      change: '+12%',
      changeType: 'increase',
      icon: 'fas fa-chart-line',
      color: 'bg-green-500'
    },
    {
      title: 'Monthly Spend',
      value: '$15,420',
      change: '-3.2%',
      changeType: 'decrease',
      icon: 'fas fa-dollar-sign',
      color: 'bg-yellow-500'
    },
    {
      title: 'Uptime',
      value: '99.9%',
      change: '0%',
      changeType: 'neutral',
      icon: 'fas fa-clock',
      color: 'bg-purple-500'
    }
  ]);

  quickActions = signal<QuickAction[]>([
    {
      title: 'Order Service',
      description: 'Request new network services',
      icon: 'fas fa-plus',
      route: '/provisioning/new',
      color: 'bg-blue-500'
    },
    {
      title: 'Manage Bandwidth',
      description: 'Adjust bandwidth allocation',
      icon: 'fas fa-sliders-h',
      route: '/bandwidth',
      color: 'bg-green-500'
    },
    {
      title: 'View Reports',
      description: 'Access usage and billing reports',
      icon: 'fas fa-chart-bar',
      route: '/reports',
      color: 'bg-purple-500'
    },
    {
      title: 'Support',
      description: 'Get help and support',
      icon: 'fas fa-headset',
      route: '/support',
      color: 'bg-orange-500'
    }
  ]);

  recentServices = signal<ServiceSummary[]>([]);
  recentOrders = signal<OrderResponse[]>([]);
  orderStatistics = signal<OrderStatistics | null>(null);

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private async loadDashboardData(): Promise<void> {
    // Load all dashboard data in parallel
    await Promise.all([
      this.loadOrderStatistics(),
      this.loadRecentOrders(),
      this.loadPopularServices()
    ]);
  }

  private async loadOrderStatistics(): Promise<void> {
    try {
      this.isLoadingStats.set(true);
      this.statsError.set(null);

      const stats = await this.orderService.getOrderStatistics().toPromise();
      this.orderStatistics.set(stats || null);

      // Update dashboard cards with real data
      if (stats) {
        this.updateDashboardCards(stats);
      }
    } catch (error: any) {
      this.statsError.set(error.message || 'Failed to load statistics');
      console.error('Error loading order statistics:', error);
    } finally {
      this.isLoadingStats.set(false);
    }
  }

  async loadRecentOrders(): Promise<void> {
    try {
      this.isLoadingOrders.set(true);
      this.ordersError.set(null);

      const orders = await this.orderService.getRecentOrders(5).toPromise();
      this.recentOrders.set(orders || []);
    } catch (error: any) {
      this.ordersError.set(error.message || 'Failed to load recent orders');
      console.error('Error loading recent orders:', error);
    } finally {
      this.isLoadingOrders.set(false);
    }
  }

  async loadPopularServices(): Promise<void> {
    try {
      this.isLoadingServices.set(true);
      this.servicesError.set(null);

      const services = await this.serviceCatalogService.getPopularServices(4).toPromise();
      this.recentServices.set(services || []);
    } catch (error: any) {
      this.servicesError.set(error.message || 'Failed to load services');
      console.error('Error loading popular services:', error);
    } finally {
      this.isLoadingServices.set(false);
    }
  }

  private updateDashboardCards(stats: OrderStatistics): void {
    this.dashboardCards.set([
      {
        title: 'Total Orders Value',
        value: `$${stats.totalOrderValue.toLocaleString()}`,
        change: '+12%',
        changeType: 'increase',
        icon: 'fas fa-dollar-sign',
        color: 'bg-green-500'
      },
      {
        title: 'Pending Orders',
        value: stats.pendingOrdersCount,
        change: '+3',
        changeType: 'increase',
        icon: 'fas fa-clock',
        color: 'bg-yellow-500'
      },
      {
        title: 'Recent Orders',
        value: stats.recentOrdersCount,
        change: 'Last 30 days',
        changeType: 'neutral',
        icon: 'fas fa-shopping-cart',
        color: 'bg-blue-500'
      },
      {
        title: 'Active Services',
        value: this.recentServices().length,
        change: 'All operational',
        changeType: 'neutral',
        icon: 'fas fa-network-wired',
        color: 'bg-purple-500'
      }
    ]);
  }

  // Helper methods for template
  getRelativeTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInHours < 1) {
      return 'Just now';
    } else if (diffInHours < 24) {
      return `${diffInHours} hour${diffInHours > 1 ? 's' : ''} ago`;
    } else if (diffInDays < 7) {
      return `${diffInDays} day${diffInDays > 1 ? 's' : ''} ago`;
    } else {
      return date.toLocaleDateString();
    }
  }

  getStatusColor(status: string): string {
    switch (status.toLowerCase()) {
      case 'completed':
        return 'bg-green-100 text-green-800';
      case 'in_progress':
      case 'provisioning':
        return 'bg-yellow-100 text-yellow-800';
      case 'submitted':
      case 'pending':
        return 'bg-blue-100 text-blue-800';
      case 'cancelled':
      case 'failed':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  refreshData(): void {
    this.loadDashboardData();
  }
}
