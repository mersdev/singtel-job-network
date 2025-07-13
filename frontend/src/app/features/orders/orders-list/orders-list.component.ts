import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService, OrderResponse, OrderSearchParams, OrderStatus, OrderType } from '../../../core/api/order.service';

@Component({
  selector: 'app-orders-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div class="flex justify-between items-center">
          <div>
            <h1 data-cy="orders-header" class="text-2xl font-bold text-gray-900 mb-2">Orders</h1>
            <p data-cy="orders-description" class="text-gray-600">
              View and manage your service orders.
            </p>
          </div>
          <button
            data-cy="new-order-button"
            (click)="createNewOrder()"
            class="px-4 py-2 bg-singtel-blue text-white font-medium rounded-md hover:bg-blue-700 transition-colors"
          >
            New Order
          </button>
        </div>
      </div>

      <!-- Filters -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div data-cy="date-range-filter" class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
          <div>
            <label for="statusFilter" class="block text-sm font-medium text-gray-700 mb-2">Status</label>
            <select
              id="statusFilter"
              data-cy="status-filter"
              [(ngModel)]="selectedStatus"
              (change)="onFilterChange()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            >
              <option value="">All Statuses</option>
              <option value="SUBMITTED">Submitted</option>
              <option value="APPROVED">Approved</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
              <option value="FAILED">Failed</option>
            </select>
          </div>

          <div>
            <label for="typeFilter" class="block text-sm font-medium text-gray-700 mb-2">Order Type</label>
            <select
              id="typeFilter"
              data-cy="order-type-filter"
              [(ngModel)]="selectedType"
              (change)="onFilterChange()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            >
              <option value="">All Types</option>
              <option value="NEW_SERVICE">New Service</option>
              <option value="UPGRADE">Upgrade</option>
              <option value="DOWNGRADE">Downgrade</option>
              <option value="MODIFICATION">Modification</option>
              <option value="CANCELLATION">Cancellation</option>
            </select>
          </div>

          <div>
            <label for="startDate" class="block text-sm font-medium text-gray-700 mb-2">From Date</label>
            <input
              type="date"
              id="startDate"
              data-cy="start-date-input"
              [(ngModel)]="startDate"
              (change)="onFilterChange()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            />
          </div>

          <div>
            <label for="endDate" class="block text-sm font-medium text-gray-700 mb-2">To Date</label>
            <input
              type="date"
              id="endDate"
              data-cy="end-date-input"
              [(ngModel)]="endDate"
              (change)="onFilterChange()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            />
          </div>

          <div class="flex items-end">
            <button
              data-cy="apply-date-filter"
              (click)="onFilterChange()"
              class="px-4 py-2 bg-singtel-blue text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              Apply Filters
            </button>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
          <div>
            <label for="searchInput" class="block text-sm font-medium text-gray-700 mb-2">Search Orders</label>
            <div class="flex space-x-2">
              <input
                type="text"
                id="searchInput"
                data-cy="search-input"
                [(ngModel)]="searchQuery"
                (input)="onSearchChange()"
                placeholder="Search by service name..."
                class="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              />
              <button
                data-cy="search-button"
                (click)="onSearchChange()"
                class="px-4 py-2 bg-singtel-blue text-white rounded-md hover:bg-blue-700 transition-colors"
              >
                Search
              </button>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Date Range</label>
            <div data-cy="date-range-filter" class="flex items-center space-x-2">
              <button
                data-cy="apply-date-filter"
                (click)="onFilterChange()"
                class="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors"
              >
                Apply Date Filter
              </button>
            </div>
          </div>
        </div>

        <div class="flex justify-end">
          <button
            data-cy="clear-filters"
            (click)="clearFilters()"
            class="px-4 py-2 text-gray-600 hover:text-gray-800 text-sm font-medium"
          >
            Clear Filters
          </button>
        </div>
      </div>

      <!-- Loading State -->
      @if (isLoading()) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <div data-cy="loading-spinner" class="animate-spin rounded-full h-12 w-12 border-b-2 border-singtel-blue mx-auto"></div>
          <p class="mt-4 text-gray-600">Loading orders...</p>
        </div>
      }

      <!-- Error State -->
      @if (error()) {
        <div class="bg-red-50 border border-red-200 rounded-lg p-6">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <h3 data-cy="error-message" class="text-sm font-medium text-red-800">Error loading orders</h3>
              <p class="mt-1 text-sm text-red-700">{{ error() }}</p>
              <button
                data-cy="retry-button"
                (click)="loadOrders()"
                class="mt-2 text-sm font-medium text-red-800 hover:text-red-900 underline"
              >
                Try again
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Orders Table -->
      @if (!isLoading() && !error() && orders().length > 0) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-200">
            <h2 class="text-lg font-medium text-gray-900">
              Orders ({{ totalOrders() }} found)
            </h2>
          </div>

          <div class="overflow-x-auto">
            <table data-cy="orders-table" class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Order
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Service
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Type
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Total
                  </th>
                  <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                @for (order of orders(); track order.id) {
                  <tr data-cy="order-row" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div data-cy="order-id" class="text-sm font-medium text-gray-900">{{ order.orderNumber }}</div>
                        <div class="text-sm text-gray-500">{{ order.id.substring(0, 8) }}...</div>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <div>
                        <div data-cy="service-name" class="text-sm font-medium text-gray-900">{{ order.service.name }}</div>
                        <div class="text-sm text-gray-500">{{ order.service.serviceType }}</div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span data-cy="order-type" class="text-sm text-gray-900">{{ order.orderType.replace('_', ' ') }}</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        data-cy="order-status"
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        [class]="getStatusColor(order.status)"
                      >
                        {{ order.status.replace('_', ' ') }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <span data-cy="order-date">{{ formatDate(order.createdAt) }}</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <span data-cy="order-cost">\${{ order.totalCost.toLocaleString() }}</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <div class="flex justify-end space-x-2">
                        <button
                          data-cy="view-button"
                          (click)="viewOrder(order.id)"
                          class="text-singtel-blue hover:text-blue-700"
                        >
                          View
                        </button>
                        @if (order.status === 'SUBMITTED' || order.status === 'APPROVED') {
                          <button
                            data-cy="cancel-button"
                            (click)="cancelOrder(order.id)"
                            class="text-red-600 hover:text-red-700"
                          >
                            Cancel
                          </button>
                        }
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
          
          <!-- Pagination -->
          @if (totalPages() > 1) {
            <div data-cy="pagination" class="bg-white px-4 py-3 border-t border-gray-200 sm:px-6">
              <div class="flex items-center justify-between">
                <div class="flex-1 flex justify-between sm:hidden">
                  <button
                    (click)="goToPage(currentPage() - 1)"
                    [disabled]="currentPage() === 1"
                    class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Previous
                  </button>
                  <button
                    data-cy="next-page"
                    (click)="goToPage(currentPage() + 1)"
                    [disabled]="currentPage() === totalPages()"
                    class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Next
                  </button>
                </div>
                <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                  <div>
                    <p class="text-sm text-gray-700">
                      Showing
                      <span class="font-medium">{{ (currentPage() - 1) * pageSize + 1 }}</span>
                      to
                      <span class="font-medium">{{ Math.min(currentPage() * pageSize, totalOrders()) }}</span>
                      of
                      <span class="font-medium">{{ totalOrders() }}</span>
                      results
                    </p>
                  </div>
                  <div>
                    <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
                      <button
                        (click)="goToPage(currentPage() - 1)"
                        [disabled]="currentPage() === 1"
                        class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        Previous
                      </button>
                      
                      @for (page of visiblePages(); track page) {
                        <button
                          data-cy="current-page"
                          (click)="goToPage(page)"
                          [class.bg-singtel-blue]="page === currentPage()"
                          [class.text-white]="page === currentPage()"
                          [class.bg-white]="page !== currentPage()"
                          [class.text-gray-500]="page !== currentPage()"
                          class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium hover:bg-gray-50"
                        >
                          {{ page }}
                        </button>
                      }

                      <button
                        data-cy="next-page"
                        (click)="goToPage(currentPage() + 1)"
                        [disabled]="currentPage() === totalPages()"
                        class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        Next
                      </button>
                    </nav>
                  </div>
                </div>
              </div>
            </div>
          }
        </div>
      }

      <!-- Empty State -->
      @if (!isLoading() && !error() && orders().length === 0) {
        <div data-cy="empty-orders" class="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">No orders found</h3>
          <p class="mt-1 text-sm text-gray-500">Get started by creating your first order.</p>
          <div class="mt-6">
            <button
              (click)="createNewOrder()"
              class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-singtel-blue hover:bg-blue-700"
            >
              Create Order
            </button>
          </div>
        </div>
      }

      <!-- Cancel Order Confirmation Dialog -->
      @if (showCancelDialog()) {
        <div class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div class="mt-3 text-center">
              <h3 class="text-lg font-medium text-gray-900">Cancel Order</h3>
              <div class="mt-2 px-7 py-3">
                <p class="text-sm text-gray-500">
                  Are you sure you want to cancel this order? This action cannot be undone.
                </p>
              </div>
              <div class="flex justify-center space-x-4 mt-4">
                <button
                  (click)="showCancelDialog.set(false)"
                  class="px-4 py-2 bg-gray-300 text-gray-800 text-base font-medium rounded-md shadow-sm hover:bg-gray-400"
                >
                  Cancel
                </button>
                <button
                  data-cy="confirm-cancel"
                  (click)="confirmCancelOrder()"
                  class="px-4 py-2 bg-red-600 text-white text-base font-medium rounded-md shadow-sm hover:bg-red-700"
                >
                  Confirm Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class OrdersListComponent implements OnInit {
  private orderService = inject(OrderService);
  private router = inject(Router);

  // Signals for reactive state management
  orders = signal<OrderResponse[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);

  // Pagination
  currentPage = signal(1);
  totalPages = signal(1);
  totalOrders = signal(0);
  pageSize = 20;

  // Filters
  selectedStatus = '';
  selectedType = '';
  startDate = '';
  endDate = '';
  searchQuery = '';

  // Cancel order dialog
  showCancelDialog = signal(false);
  orderToCancel = signal<string | null>(null);

  // Computed values
  visiblePages = signal<number[]>([]);
  Math = Math; // Make Math available in template

  ngOnInit(): void {
    this.loadOrders();
  }

  async loadOrders(): Promise<void> {
    try {
      this.isLoading.set(true);
      this.error.set(null);

      const searchParams: OrderSearchParams = {
        page: this.currentPage(),
        limit: this.pageSize
      };

      // Add filters
      if (this.selectedStatus) {
        searchParams.status = this.selectedStatus as OrderStatus;
      }
      if (this.selectedType) {
        searchParams.orderType = this.selectedType as OrderType;
      }
      if (this.startDate) {
        searchParams.startDate = this.startDate;
      }
      if (this.endDate) {
        searchParams.endDate = this.endDate;
      }

      const response = await this.orderService.searchOrders(searchParams).toPromise();

      if (response && response.data) {
        const orders = Array.isArray(response.data) ? response.data : [];
        this.orders.set(orders);
        this.totalOrders.set(response.pagination?.total || orders.length);
        this.totalPages.set(response.pagination?.totalPages || 1);
        this.updateVisiblePages();
      } else {
        this.orders.set([]);
        this.totalOrders.set(0);
        this.totalPages.set(1);
        this.updateVisiblePages();
      }
    } catch (error: any) {
      console.error('Error loading orders:', error);
      this.error.set(error.message || 'Failed to load orders');
      this.orders.set([]);
      this.totalOrders.set(0);
      this.totalPages.set(1);
      this.updateVisiblePages();
    } finally {
      this.isLoading.set(false);
    }
  }

  private updateVisiblePages(): void {
    const current = this.currentPage();
    const total = this.totalPages();
    const pages: number[] = [];

    const start = Math.max(1, current - 2);
    const end = Math.min(total, current + 2);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    this.visiblePages.set(pages);
  }

  onFilterChange(): void {
    this.currentPage.set(1);
    this.loadOrders();
  }

  onSearchChange(): void {
    this.currentPage.set(1);
    this.loadOrders();
  }

  clearFilters(): void {
    this.selectedStatus = '';
    this.selectedType = '';
    this.startDate = '';
    this.endDate = '';
    this.searchQuery = '';
    this.currentPage.set(1);
    this.loadOrders();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
      this.updateVisiblePages();
      this.loadOrders();
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'IN_PROGRESS':
        return 'bg-yellow-100 text-yellow-800';
      case 'SUBMITTED':
        return 'bg-blue-100 text-blue-800';
      case 'APPROVED':
        return 'bg-indigo-100 text-indigo-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  viewOrder(orderId: string): void {
    this.router.navigate(['/orders', orderId]);
  }

  cancelOrder(orderId: string): void {
    this.orderToCancel.set(orderId);
    this.showCancelDialog.set(true);
  }

  async confirmCancelOrder(): Promise<void> {
    const orderId = this.orderToCancel();
    if (!orderId) return;

    try {
      await this.orderService.cancelOrder(orderId).toPromise();
      this.showCancelDialog.set(false);
      this.orderToCancel.set(null);

      // Show success message
      this.showSuccessMessage('Order cancelled successfully');

      // Reload orders to reflect the change
      this.loadOrders();
    } catch (error: any) {
      this.error.set('Failed to cancel order: ' + (error.message || 'Unknown error'));
      this.showCancelDialog.set(false);
      this.orderToCancel.set(null);
    }
  }

  private showSuccessMessage(message: string): void {
    // For now, we'll use a simple approach. In a real app, you'd use a toast service
    const successDiv = document.createElement('div');
    successDiv.setAttribute('data-cy', 'success-message');
    successDiv.className = 'fixed top-4 right-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded z-50';
    successDiv.textContent = message;
    document.body.appendChild(successDiv);

    setTimeout(() => {
      document.body.removeChild(successDiv);
    }, 3000);
  }

  createNewOrder(): void {
    this.router.navigate(['/provisioning/new']);
  }
}
