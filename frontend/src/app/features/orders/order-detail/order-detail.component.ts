import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService, OrderResponse } from '../../../core/api/order.service';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <!-- Loading State -->
      @if (isLoading()) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-singtel-blue mx-auto"></div>
          <p class="mt-4 text-gray-600">Loading order details...</p>
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
              <h3 class="text-sm font-medium text-red-800">Error loading order</h3>
              <p class="mt-1 text-sm text-red-700">{{ error() }}</p>
              <button
                (click)="loadOrderDetail()"
                class="mt-2 text-sm font-medium text-red-800 hover:text-red-900 underline"
              >
                Try again
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Order Details -->
      @if (!isLoading() && !error() && order()) {
        <div data-cy="order-details" class="bg-white rounded-lg shadow-sm border border-gray-200">
          <!-- Header -->
          <div class="px-6 py-4 border-b border-gray-200">
            <div class="flex justify-between items-start">
              <div>
                <h1 class="text-2xl font-bold text-gray-900">Order {{ order()?.orderNumber }}</h1>
                <p data-cy="order-id" class="mt-1 text-gray-600">Order ID: {{ order()?.id }}</p>
              </div>
              <div class="text-right">
                <span
                  data-cy="order-status"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
                  [class]="getStatusColor(order()?.status || '')"
                >
                  {{ order()?.status?.replace('_', ' ') | titlecase }}
                </span>
              </div>
            </div>
          </div>

          <!-- Order Information -->
          <div class="p-6">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <!-- Service Information -->
              <div data-cy="service-details">
                <h3 class="text-lg font-medium text-gray-900 mb-4">Service Information</h3>
                <dl class="space-y-3">
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Service Name</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.service?.name }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Service Type</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.service?.serviceType }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Order Type</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.orderType?.replace('_', ' ') | titlecase }}</dd>
                  </div>
                  @if (order()?.requestedBandwidthMbps) {
                    <div>
                      <dt class="text-sm font-medium text-gray-500">Requested Bandwidth</dt>
                      <dd class="text-sm text-gray-900">{{ order()?.requestedBandwidthMbps }} Mbps</dd>
                    </div>
                  }
                </dl>
              </div>

              <!-- Contact Information -->
              <div>
                <h3 class="text-lg font-medium text-gray-900 mb-4">Contact Information</h3>
                <dl class="space-y-3">
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Contact Person</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.contactPerson }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Phone</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.contactPhone }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Email</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.contactEmail }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Installation Address</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.installationAddress }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Postal Code</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.postalCode }}</dd>
                  </div>
                </dl>
              </div>
            </div>

            <!-- Dates and Cost -->
            <div class="mt-8 grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div data-cy="order-timeline">
                <h3 class="text-lg font-medium text-gray-900 mb-4">Timeline</h3>
                <dl class="space-y-3">
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Order Date</dt>
                    <dd class="text-sm text-gray-900">{{ formatDate(order()?.createdAt || '') }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Requested Date</dt>
                    <dd class="text-sm text-gray-900">{{ formatDate(order()?.requestedDate || '') }}</dd>
                  </div>
                  @if (order()?.estimatedCompletionDate) {
                    <div>
                      <dt class="text-sm font-medium text-gray-500">Estimated Completion</dt>
                      <dd class="text-sm text-gray-900">{{ formatDate(order()?.estimatedCompletionDate || '') }}</dd>
                    </div>
                  }
                  @if (order()?.actualCompletionDate) {
                    <div>
                      <dt class="text-sm font-medium text-gray-500">Actual Completion</dt>
                      <dd class="text-sm text-gray-900">{{ formatDate(order()?.actualCompletionDate || '') }}</dd>
                    </div>
                  }
                </dl>
              </div>

              <div data-cy="billing-information">
                <h3 class="text-lg font-medium text-gray-900 mb-4">Cost Information</h3>
                <dl class="space-y-3">
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Total Cost</dt>
                    <dd class="text-lg font-bold text-singtel-blue">\${{ order()?.totalCost?.toLocaleString() }}</dd>
                  </div>
                </dl>
              </div>
            </div>

            <!-- Notes -->
            @if (order()?.notes) {
              <div class="mt-8">
                <h3 class="text-lg font-medium text-gray-900 mb-4">Notes</h3>
                <p class="text-sm text-gray-700 bg-gray-50 p-4 rounded-lg">{{ order()?.notes }}</p>
              </div>
            }

            <!-- Service Instance -->
            @if (order()?.serviceInstance) {
              <div class="mt-8">
                <h3 class="text-lg font-medium text-gray-900 mb-4">Service Instance</h3>
                <dl class="space-y-3">
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Instance Name</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.serviceInstance?.instanceName }}</dd>
                  </div>
                  <div>
                    <dt class="text-sm font-medium text-gray-500">Status</dt>
                    <dd class="text-sm text-gray-900">{{ order()?.serviceInstance?.status }}</dd>
                  </div>
                </dl>
              </div>
            }
          </div>

          <!-- Actions -->
          <div class="px-6 py-4 border-t border-gray-200 flex justify-between">
            <button
              (click)="goBack()"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
            >
              Back to Orders
            </button>
            
            @if (canCancelOrder()) {
              <button
                (click)="cancelOrder()"
                class="px-4 py-2 text-sm font-medium text-white bg-red-600 border border-transparent rounded-md hover:bg-red-700"
              >
                Cancel Order
              </button>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class OrderDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private orderService = inject(OrderService);

  order = signal<OrderResponse | null>(null);
  isLoading = signal(false);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const orderId = params['id'];
      if (orderId) {
        this.loadOrderDetail(orderId);
      }
    });
  }

  async loadOrderDetail(orderId?: string): Promise<void> {
    const id = orderId || this.route.snapshot.params['id'];
    if (!id) return;

    try {
      this.isLoading.set(true);
      this.error.set(null);

      const order = await this.orderService.getOrderById(id).toPromise();
      this.order.set(order || null);
    } catch (error: any) {
      console.error('Error loading order detail:', error);
      this.error.set(error.message || 'Failed to load order details');
    } finally {
      this.isLoading.set(false);
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
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString();
  }

  canCancelOrder(): boolean {
    const status = this.order()?.status;
    return status === 'SUBMITTED' || status === 'APPROVED';
  }

  async cancelOrder(): Promise<void> {
    const order = this.order();
    if (!order || !this.canCancelOrder()) return;

    if (confirm('Are you sure you want to cancel this order?')) {
      try {
        await this.orderService.cancelOrder(order.id).toPromise();
        // Reload order to reflect the change
        this.loadOrderDetail();
      } catch (error: any) {
        alert('Failed to cancel order: ' + (error.message || 'Unknown error'));
      }
    }
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }
}
