import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/api/order.service';
import { OrderResponse } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center space-x-4">
          <button
            (click)="goBack()"
            class="flex items-center text-gray-600 hover:text-gray-800 transition-colors"
          >
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
            Back to Orders
          </button>
          <h1 class="text-3xl font-bold text-gray-900">Order Details</h1>
        </div>
        
        @if (order() && canCancelOrder()) {
          <button
            (click)="cancelOrder()"
            [disabled]="isLoading()"
            class="bg-red-600 hover:bg-red-700 disabled:bg-gray-400 text-white px-4 py-2 rounded-md transition-colors"
          >
            Cancel Order
          </button>
        }
      </div>

      <!-- Loading State -->
      @if (isLoading()) {
        <div class="flex justify-center items-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-singtel-blue"></div>
        </div>
      }

      <!-- Error State -->
      @if (error()) {
        <div class="bg-red-50 border border-red-200 rounded-md p-4 mb-6">
          <div class="flex">
            <svg class="w-5 h-5 text-red-400 mr-3 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
            <div>
              <h3 class="text-sm font-medium text-red-800">Error</h3>
              <p class="text-sm text-red-700 mt-1">{{ error() }}</p>
            </div>
          </div>
        </div>
      }

      <!-- Order Details -->
      @if (order() && !isLoading()) {
        <div class="bg-white shadow-lg rounded-lg overflow-hidden">
          <!-- Order Header -->
          <div class="bg-gray-50 px-6 py-4 border-b border-gray-200">
            <div class="flex items-center justify-between">
              <div>
                <h2 class="text-xl font-semibold text-gray-900">{{ order()!.orderNumber }}</h2>
                <p class="text-sm text-gray-600 mt-1">{{ getOrderTypeDisplay() }}</p>
              </div>
              <div class="text-right">
                <span [class]="getStatusBadgeClass()" class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium">
                  {{ getStatusDisplay() }}
                </span>
                <p class="text-sm text-gray-600 mt-1">Created {{ formatDate(order()!.createdAt) }}</p>
              </div>
            </div>
          </div>

          <!-- Order Content -->
          <div class="p-6">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <!-- Service Information -->
              <div>
                <h3 class="text-lg font-medium text-gray-900 mb-4">Service Information</h3>
                <div class="space-y-3">
                  @if (order()!.service) {
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Service</label>
                      <p class="mt-1 text-sm text-gray-900">{{ order()!.service.name }}</p>
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Service Type</label>
                      <p class="mt-1 text-sm text-gray-900">{{ order()!.service.serviceType }}</p>
                    </div>
                  }
                  @if (order()!.requestedBandwidthMbps) {
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Requested Bandwidth</label>
                      <p class="mt-1 text-sm text-gray-900">{{ order()!.requestedBandwidthMbps }} Mbps</p>
                    </div>
                  }
                </div>
              </div>

              <!-- Installation Details -->
              <div>
                <h3 class="text-lg font-medium text-gray-900 mb-4">Installation Details</h3>
                <div class="space-y-3">
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Installation Address</label>
                    <p class="mt-1 text-sm text-gray-900">{{ order()!.installationAddress }}</p>
                  </div>
                  @if (order()!.postalCode) {
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Postal Code</label>
                      <p class="mt-1 text-sm text-gray-900">{{ order()!.postalCode }}</p>
                    </div>
                  }
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Requested Date</label>
                    <p class="mt-1 text-sm text-gray-900">{{ formatDate(order()!.requestedDate) }}</p>
                  </div>
                  @if (order()!.estimatedCompletionDate) {
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Estimated Completion</label>
                      <p class="mt-1 text-sm text-gray-900">{{ formatDate(order()!.estimatedCompletionDate) }}</p>
                    </div>
                  }
                </div>
              </div>

              <!-- Contact Information -->
              <div>
                <h3 class="text-lg font-medium text-gray-900 mb-4">Contact Information</h3>
                <div class="space-y-3">
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Contact Person</label>
                    <p class="mt-1 text-sm text-gray-900">{{ order()!.contactPerson }}</p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Phone</label>
                    <p class="mt-1 text-sm text-gray-900">{{ order()!.contactPhone }}</p>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Email</label>
                    <p class="mt-1 text-sm text-gray-900">{{ order()!.contactEmail }}</p>
                  </div>
                </div>
              </div>

              <!-- Order Summary -->
              <div>
                <h3 class="text-lg font-medium text-gray-900 mb-4">Order Summary</h3>
                <div class="space-y-3">
                  @if (order()!.totalCost) {
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Total Cost</label>
                      <p class="mt-1 text-lg font-semibold text-gray-900">${{ order()!.totalCost }}</p>
                    </div>
                  }
                  @if (order()!.notes) {
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Notes</label>
                      <p class="mt-1 text-sm text-gray-900">{{ order()!.notes }}</p>
                    </div>
                  }
                </div>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .container {
      max-width: 1200px;
    }
  `]
})
export class OrderDetailsComponent implements OnInit {
  // Signals for reactive state management
  order = signal<OrderResponse | null>(null);
  isLoading = signal(false);
  error = signal<string | null>(null);

  // Computed properties
  canCancelOrder = computed(() => {
    const currentOrder = this.order();
    return currentOrder && (
      currentOrder.status === 'SUBMITTED' || 
      currentOrder.status === 'APPROVED'
    );
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.loadOrderDetails(orderId);
    } else {
      this.error.set('Order ID not provided');
    }
  }

  async loadOrderDetails(orderId: string): Promise<void> {
    try {
      this.isLoading.set(true);
      this.error.set(null);

      const order = await this.orderService.getOrderById(orderId).toPromise();
      this.order.set(order);
    } catch (error: any) {
      console.error('Error loading order details:', error);
      this.error.set(error.message || 'Failed to load order details');
    } finally {
      this.isLoading.set(false);
    }
  }

  async cancelOrder(): Promise<void> {
    const currentOrder = this.order();
    if (!currentOrder) return;

    try {
      this.isLoading.set(true);
      await this.orderService.cancelOrder(currentOrder.id).toPromise();
      
      // Reload order details to show updated status
      await this.loadOrderDetails(currentOrder.id);
    } catch (error: any) {
      console.error('Error cancelling order:', error);
      this.error.set(error.message || 'Failed to cancel order');
    } finally {
      this.isLoading.set(false);
    }
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }

  getOrderTypeDisplay(): string {
    const orderType = this.order()?.orderType;
    switch (orderType) {
      case 'NEW_SERVICE': return 'New Service';
      case 'MODIFY_SERVICE': return 'Modify Service';
      case 'TERMINATE_SERVICE': return 'Terminate Service';
      default: return orderType || '';
    }
  }

  getStatusDisplay(): string {
    const status = this.order()?.status;
    switch (status) {
      case 'SUBMITTED': return 'Submitted';
      case 'APPROVED': return 'Approved';
      case 'IN_PROGRESS': return 'In Progress';
      case 'COMPLETED': return 'Completed';
      case 'CANCELLED': return 'Cancelled';
      case 'REJECTED': return 'Rejected';
      default: return status || '';
    }
  }

  getStatusBadgeClass(): string {
    const status = this.order()?.status;
    switch (status) {
      case 'SUBMITTED': return 'bg-blue-100 text-blue-800';
      case 'APPROVED': return 'bg-green-100 text-green-800';
      case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800';
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      case 'REJECTED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(date: string | Date | null | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
