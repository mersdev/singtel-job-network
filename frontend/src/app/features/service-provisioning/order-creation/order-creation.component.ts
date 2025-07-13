import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService, CreateOrderRequest, OrderType } from '../../../core/api/order.service';
import { ServiceCatalogService, ServiceDetail } from '../../../core/api/service-catalog.service';

@Component({
  selector: 'app-order-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-2">Order New Service</h1>
        <p class="text-gray-600">
          Complete the form below to place an order for network services.
        </p>
      </div>

      <!-- Service Selection -->
      @if (selectedService()) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Selected Service</h2>
          <div class="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
            <div>
              <h3 class="font-medium text-gray-900">{{ selectedService()?.name }}</h3>
              <p class="text-sm text-gray-600">{{ selectedService()?.description }}</p>
              <p class="text-sm text-gray-500">Category: {{ selectedService()?.categoryName }}</p>
            </div>
            <div class="text-right">
              <p class="text-lg font-bold text-singtel-blue">\${{ selectedService()?.monthlyPrice }}/month</p>
              @if (selectedService()?.setupFee && (selectedService()?.setupFee || 0) > 0) {
                <p class="text-sm text-gray-500">Setup: \${{ selectedService()?.setupFee }}</p>
              }
            </div>
          </div>
        </div>
      } @else {
        <!-- Service Selection Dropdown -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Select Service</h2>
          <div>
            <label for="serviceSelect" class="block text-sm font-medium text-gray-700 mb-2">Service</label>
            <select
              id="serviceSelect"
              data-cy="service-select"
              (change)="onServiceSelect($event)"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            >
              <option value="">Select a service</option>
              <option value="mpls">MPLS Network Service</option>
              <option value="internet">Internet Leased Line</option>
              <option value="vpn">VPN Service</option>
            </select>
          </div>
        </div>
      }

      <!-- Order Form -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-6">Order Details</h2>
        
        <form [formGroup]="orderForm" (ngSubmit)="onSubmit()" class="space-y-6">
          <!-- Order Type -->
          <div>
            <label for="orderType" class="block text-sm font-medium text-gray-700 mb-2">Order Type</label>
            <select
              id="orderType"
              formControlName="orderType"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            >
              <option value="NEW_SERVICE">New Service</option>
              <option value="UPGRADE">Upgrade</option>
              <option value="MODIFICATION">Modification</option>
            </select>
          </div>

          <!-- Bandwidth (if applicable) -->
          @if (selectedService()?.isBandwidthAdjustable) {
            <div>
              <label for="requestedBandwidthMbps" class="block text-sm font-medium text-gray-700 mb-2">
                Requested Bandwidth (Mbps)
              </label>
              <select
                id="requestedBandwidthMbps"
                data-cy="bandwidth-input"
                formControlName="requestedBandwidthMbps"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              >
                <option value="">Select bandwidth</option>
                @for (bandwidth of selectedService()?.supportedBandwidths || []; track bandwidth) {
                  <option [value]="bandwidth">{{ bandwidth }} Mbps</option>
                }
              </select>
            </div>
          } @else {
            <!-- Manual bandwidth input for testing -->
            <div>
              <label for="requestedBandwidthMbps" class="block text-sm font-medium text-gray-700 mb-2">
                Requested Bandwidth (Mbps)
              </label>
              <input
                type="number"
                id="requestedBandwidthMbps"
                data-cy="bandwidth-input"
                formControlName="requestedBandwidthMbps"
                placeholder="Enter bandwidth in Mbps"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              />
            </div>
          }

          <!-- Installation Details -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label for="installationAddress" class="block text-sm font-medium text-gray-700 mb-2">
                Installation Address *
              </label>
              <textarea
                id="installationAddress"
                data-cy="location-input"
                formControlName="installationAddress"
                rows="3"
                placeholder="Enter complete installation address"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              ></textarea>
              @if (orderForm.get('installationAddress')?.invalid && orderForm.get('installationAddress')?.touched) {
                <p class="mt-1 text-sm text-red-600">Installation address is required</p>
              }
            </div>

            <div>
              <label for="postalCode" class="block text-sm font-medium text-gray-700 mb-2">
                Postal Code *
              </label>
              <input
                type="text"
                id="postalCode"
                formControlName="postalCode"
                placeholder="e.g., 569874"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              />
              @if (orderForm.get('postalCode')?.invalid && orderForm.get('postalCode')?.touched) {
                <p class="mt-1 text-sm text-red-600">Postal code is required</p>
              }
            </div>
          </div>

          <!-- Contact Information -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div>
              <label for="contactPerson" class="block text-sm font-medium text-gray-700 mb-2">
                Contact Person *
              </label>
              <input
                type="text"
                id="contactPerson"
                formControlName="contactPerson"
                placeholder="Full name"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              />
              @if (orderForm.get('contactPerson')?.invalid && orderForm.get('contactPerson')?.touched) {
                <p class="mt-1 text-sm text-red-600">Contact person is required</p>
              }
            </div>

            <div>
              <label for="contactPhone" class="block text-sm font-medium text-gray-700 mb-2">
                Contact Phone *
              </label>
              <input
                type="tel"
                id="contactPhone"
                formControlName="contactPhone"
                placeholder="+65 9123 4567"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              />
              @if (orderForm.get('contactPhone')?.invalid && orderForm.get('contactPhone')?.touched) {
                <p class="mt-1 text-sm text-red-600">Contact phone is required</p>
              }
            </div>

            <div>
              <label for="contactEmail" class="block text-sm font-medium text-gray-700 mb-2">
                Contact Email *
              </label>
              <input
                type="email"
                id="contactEmail"
                formControlName="contactEmail"
                placeholder="email@company.com"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
              />
              @if (orderForm.get('contactEmail')?.invalid && orderForm.get('contactEmail')?.touched) {
                <p class="mt-1 text-sm text-red-600">Valid email is required</p>
              }
            </div>
          </div>

          <!-- Requested Date -->
          <div>
            <label for="requestedDate" class="block text-sm font-medium text-gray-700 mb-2">
              Requested Installation Date *
            </label>
            <input
              type="date"
              id="requestedDate"
              formControlName="requestedDate"
              [min]="minDate"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            />
            @if (orderForm.get('requestedDate')?.invalid && orderForm.get('requestedDate')?.touched) {
              <p class="mt-1 text-sm text-red-600">Requested date is required</p>
            }
          </div>

          <!-- Special Requirements -->
          <div>
            <label for="specialRequirements" class="block text-sm font-medium text-gray-700 mb-2">
              Special Requirements (Optional)
            </label>
            <textarea
              id="specialRequirements"
              data-cy="notes-input"
              formControlName="specialRequirements"
              rows="3"
              placeholder="Any special requirements or notes for the installation"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            ></textarea>
          </div>

          <!-- Error Message -->
          @if (error()) {
            <div class="bg-red-50 border border-red-200 rounded-lg p-4">
              <div class="flex">
                <div class="flex-shrink-0">
                  <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                  </svg>
                </div>
                <div class="ml-3">
                  <h3 class="text-sm font-medium text-red-800">Error submitting order</h3>
                  <p class="mt-1 text-sm text-red-700">{{ error() }}</p>
                </div>
              </div>
            </div>
          }

          <!-- Form Actions -->
          <div class="flex justify-between items-center pt-6 border-t border-gray-200">
            <button
              type="button"
              (click)="goBack()"
              class="px-6 py-3 border border-gray-300 text-gray-700 font-medium rounded-md hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              data-cy="submit-order-button"
              [disabled]="orderForm.invalid || isSubmitting()"
              class="px-6 py-3 bg-singtel-blue text-white font-medium rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              @if (isSubmitting()) {
                <span class="flex items-center">
                  <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Creating Order...
                </span>
              } @else {
                Submit Order
              }
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class OrderCreationComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private orderService = inject(OrderService);
  private serviceCatalogService = inject(ServiceCatalogService);

  selectedService = signal<ServiceDetail | null>(null);
  isSubmitting = signal(false);
  error = signal<string | null>(null);

  orderForm: FormGroup;
  minDate: string;

  constructor() {
    // Set minimum date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minDate = tomorrow.toISOString().split('T')[0];

    this.orderForm = this.fb.group({
      orderType: [OrderType.NEW_SERVICE, Validators.required],
      requestedBandwidthMbps: [''],
      installationAddress: ['', Validators.required],
      postalCode: ['', Validators.required],
      contactPerson: ['', Validators.required],
      contactPhone: ['', Validators.required],
      contactEmail: ['', [Validators.required, Validators.email]],
      requestedDate: ['', Validators.required],
      specialRequirements: ['']
    });
  }

  ngOnInit(): void {
    // Check for service ID in query params
    this.route.queryParams.subscribe(params => {
      const serviceId = params['serviceId'];
      if (serviceId) {
        this.loadService(serviceId);
      }
    });
  }

  private async loadService(serviceId: string): Promise<void> {
    try {
      const service = await this.serviceCatalogService.getServiceById(serviceId).toPromise();
      this.selectedService.set(service || null);
      
      // If service requires bandwidth selection, make it required
      if (service?.isBandwidthAdjustable) {
        this.orderForm.get('requestedBandwidthMbps')?.setValidators([Validators.required]);
        this.orderForm.get('requestedBandwidthMbps')?.updateValueAndValidity();
      }
    } catch (error: any) {
      this.error.set(error.message || 'Failed to load service details');
    }
  }

  async onSubmit(): Promise<void> {
    if (this.orderForm.invalid || !this.selectedService()) {
      this.orderForm.markAllAsTouched();
      return;
    }

    try {
      this.isSubmitting.set(true);
      this.error.set(null);

      const formValue = this.orderForm.value;
      const request: CreateOrderRequest = {
        serviceId: this.selectedService()!.id,
        orderType: formValue.orderType,
        installationAddress: formValue.installationAddress,
        postalCode: formValue.postalCode,
        contactPerson: formValue.contactPerson,
        contactPhone: formValue.contactPhone,
        contactEmail: formValue.contactEmail,
        requestedDate: formValue.requestedDate,
        specialRequirements: formValue.specialRequirements || undefined
      };

      // Add bandwidth if specified
      if (formValue.requestedBandwidthMbps) {
        request.requestedBandwidthMbps = parseInt(formValue.requestedBandwidthMbps);
      }

      const order = await this.orderService.createOrder(request).toPromise();
      
      // Navigate to order confirmation or orders list
      this.router.navigate(['/orders', order?.id], {
        queryParams: { created: 'true' }
      });
    } catch (error: any) {
      this.error.set(error.message || 'Failed to create order');
    } finally {
      this.isSubmitting.set(false);
    }
  }

  onServiceSelect(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const serviceValue = target.value;

    // For testing purposes, create a mock service
    if (serviceValue === 'mpls') {
      const mockService: ServiceDetail = {
        id: 'mock-mpls-id',
        name: 'MPLS Network Service',
        description: 'High-performance MPLS network connectivity',
        categoryName: 'Connectivity',
        serviceType: 'MPLS',
        monthlyPrice: 500,
        setupFee: 100,
        isBandwidthAdjustable: true,
        supportedBandwidths: [10, 50, 100, 500, 1000],
        isAvailable: true,
        provisioningTimeHours: 72,
        contractTermMonths: 12,
        basePriceMonthly: 500,
        pricePerMbps: 5,
        availableLocations: ['Singapore', 'Malaysia'],
        baseBandwidthMbps: 10,
        maxBandwidthMbps: 1000,
        minBandwidthMbps: 10,
        features: {},
        technicalSpecs: {}
      };
      this.selectedService.set(mockService);

      // Make bandwidth required for MPLS
      this.orderForm.get('requestedBandwidthMbps')?.setValidators([Validators.required]);
      this.orderForm.get('requestedBandwidthMbps')?.updateValueAndValidity();
    }
  }

  goBack(): void {
    this.router.navigate(['/services']);
  }
}
