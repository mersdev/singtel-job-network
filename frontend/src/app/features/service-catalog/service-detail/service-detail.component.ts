import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ServiceCatalogService, ServiceDetail } from '../../../core/api/service-catalog.service';

@Component({
  selector: 'app-service-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <!-- Loading State -->
      @if (isLoading()) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-singtel-blue mx-auto"></div>
          <p class="mt-4 text-gray-600">Loading service details...</p>
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
              <h3 class="text-sm font-medium text-red-800">Error loading service details</h3>
              <p class="mt-1 text-sm text-red-700">{{ error() }}</p>
              <div class="mt-4 flex space-x-3">
                <button
                  (click)="loadServiceDetail()"
                  class="text-sm font-medium text-red-800 hover:text-red-900 underline"
                >
                  Try again
                </button>
                <button
                  (click)="goBack()"
                  class="text-sm font-medium text-red-800 hover:text-red-900 underline"
                >
                  Go back
                </button>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- Service Detail -->
      @if (!isLoading() && !error() && service()) {
        <div data-cy="service-details">
          <!-- Breadcrumb -->
          <nav class="flex" aria-label="Breadcrumb">
            <ol class="flex items-center space-x-4">
              <li>
                <div>
                  <a (click)="goBack()" class="text-gray-400 hover:text-gray-500 cursor-pointer">
                    <svg class="flex-shrink-0 h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clip-rule="evenodd" />
                    </svg>
                    <span class="sr-only">Back</span>
                  </a>
                </div>
              </li>
              <li>
                <div class="flex items-center">
                  <svg class="flex-shrink-0 h-5 w-5 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M5.555 17.776l8-16 .894.448-8 16-.894-.448z" />
                  </svg>
                  <a (click)="goBack()" class="ml-4 text-sm font-medium text-gray-500 hover:text-gray-700 cursor-pointer">Service Catalog</a>
                </div>
              </li>
              <li>
                <div class="flex items-center">
                  <svg class="flex-shrink-0 h-5 w-5 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M5.555 17.776l8-16 .894.448-8 16-.894-.448z" />
                  </svg>
                  <span class="ml-4 text-sm font-medium text-gray-500">{{ service()?.name }}</span>
                </div>
              </li>
            </ol>
          </nav>

          <!-- Service Header -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex justify-between items-start">
              <div class="flex-1">
                <div class="flex items-center space-x-3 mb-2">
                  <h1 data-cy="service-name" class="text-3xl font-bold text-gray-900">{{ service()?.name }}</h1>
                @if (service()?.isBandwidthAdjustable) {
                  <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                    Bandwidth Adjustable
                  </span>
                }
                @if (service()?.isAvailable) {
                  <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                    Available
                  </span>
                } @else {
                  <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                    Unavailable
                  </span>
                }
              </div>
              <p data-cy="service-description" class="text-lg text-gray-600 mb-4">{{ service()?.description }}</p>
              <div class="flex items-center space-x-6 text-sm text-gray-500">
                <span><strong>Category:</strong> {{ service()?.categoryName }}</span>
                <span><strong>Type:</strong> {{ service()?.serviceType }}</span>
                <span><strong>Provisioning:</strong> {{ service()?.provisioningTimeHours }} hours</span>
              </div>
            </div>
            <div class="text-right">
              <div class="text-3xl font-bold text-singtel-blue">\${{ service()?.basePriceMonthly || service()?.monthlyPrice }}/month</div>
              @if (service()?.setupFee && (service()?.setupFee || 0) > 0) {
                <div class="text-sm text-gray-500">Setup fee: \${{ service()?.setupFee }}</div>
              }
              @if (service()?.pricePerMbps) {
                <div class="text-sm text-gray-500">\${{ service()?.pricePerMbps }}/Mbps</div>
              }
              <div class="text-sm text-gray-500">{{ service()?.contractTermMonths }} month contract</div>
            </div>
          </div>
        </div>

        <!-- Service Details Grid -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Features -->
          <div data-cy="service-features" class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Features</h2>
            @if (service()?.features && hasFeatures()) {
              <div class="space-y-3">
                @for (feature of getFeatureEntries(); track feature.key) {
                  <div class="flex justify-between items-center">
                    <span class="text-gray-700 capitalize">{{ formatFeatureKey(feature.key) }}</span>
                    <span class="font-medium text-gray-900">{{ formatFeatureValue(feature.value) }}</span>
                  </div>
                }
              </div>
            } @else {
              <p class="text-gray-500">No features information available.</p>
            }
          </div>

          <!-- Technical Specifications -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Technical Specifications</h2>
            @if (service()?.technicalSpecs && hasTechnicalSpecs()) {
              <div class="space-y-3">
                @for (spec of getTechnicalSpecEntries(); track spec.key) {
                  <div class="flex justify-between items-center">
                    <span class="text-gray-700 capitalize">{{ formatFeatureKey(spec.key) }}</span>
                    <span class="font-medium text-gray-900">{{ formatFeatureValue(spec.value) }}</span>
                  </div>
                }
              </div>
            } @else {
              <p class="text-gray-500">No technical specifications available.</p>
            }
          </div>
        </div>

        <!-- Bandwidth Options -->
        @if (service()?.supportedBandwidths && (service()?.supportedBandwidths?.length || 0) > 0) {
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Available Bandwidth Options</h2>
            <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-3">
              @for (bandwidth of service()?.supportedBandwidths; track bandwidth) {
                <div class="text-center p-3 border border-gray-200 rounded-lg">
                  <div class="font-medium text-gray-900">{{ bandwidth }} Mbps</div>
                  @if (service()?.pricePerMbps) {
                    <div class="text-sm text-gray-500">\${{ (bandwidth * (service()?.pricePerMbps || 0)).toFixed(2) }}/month</div>
                  }
                </div>
              }
            </div>
          </div>
        }

        <!-- Available Locations -->
        @if (service()?.availableLocations && (service()?.availableLocations?.length || 0) > 0) {
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">Available Locations</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
              @for (location of service()?.availableLocations; track location) {
                <div class="flex items-center p-3 border border-gray-200 rounded-lg">
                  <svg class="h-5 w-5 text-green-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <span class="text-gray-900">{{ location }}</span>
                </div>
              }
            </div>
          </div>
        }

        <!-- Action Buttons -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-lg font-medium text-gray-900">Ready to get started?</h3>
              <p class="text-gray-600">Order this service or contact our team for more information.</p>
            </div>
            <div class="flex space-x-4">
              <button
                (click)="contactSupport()"
                class="px-6 py-3 border border-gray-300 text-gray-700 font-medium rounded-md hover:bg-gray-50 transition-colors"
              >
                Contact Support
              </button>
              <button
                data-cy="order-button"
                (click)="orderService()"
                [disabled]="!service()?.isAvailable"
                class="px-6 py-3 bg-singtel-blue text-white font-medium rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Order Now
              </button>
            </div>
          </div>
        </div>
        </div>
      }
    </div>
  `
})
export class ServiceDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private serviceCatalogService = inject(ServiceCatalogService);

  service = signal<ServiceDetail | null>(null);
  isLoading = signal(false);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const serviceId = params['id'];
      if (serviceId) {
        this.loadServiceDetail(serviceId);
      }
    });
  }

  async loadServiceDetail(serviceId?: string): Promise<void> {
    const id = serviceId || this.route.snapshot.params['id'];
    if (!id) return;

    try {
      this.isLoading.set(true);
      this.error.set(null);

      const service = await this.serviceCatalogService.getServiceById(id).toPromise();
      this.service.set(service || null);
    } catch (error: any) {
      this.error.set(error.message || 'Failed to load service details');
    } finally {
      this.isLoading.set(false);
    }
  }

  hasFeatures(): boolean {
    const features = this.service()?.features;
    return !!(features && Object.keys(features).length > 0);
  }

  hasTechnicalSpecs(): boolean {
    const specs = this.service()?.technicalSpecs;
    return !!(specs && Object.keys(specs).length > 0);
  }

  getFeatureEntries(): Array<{key: string, value: any}> {
    const features = this.service()?.features || {};
    return Object.entries(features).map(([key, value]) => ({ key, value }));
  }

  getTechnicalSpecEntries(): Array<{key: string, value: any}> {
    const specs = this.service()?.technicalSpecs || {};
    return Object.entries(specs).map(([key, value]) => ({ key, value }));
  }

  formatFeatureKey(key: string): string {
    return key.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
  }

  formatFeatureValue(value: any): string {
    if (typeof value === 'boolean') {
      return value ? 'Yes' : 'No';
    }
    return String(value);
  }

  goBack(): void {
    this.router.navigate(['/services']);
  }

  orderService(): void {
    const serviceId = this.service()?.id;
    if (serviceId) {
      this.router.navigate(['/provisioning/new'], { 
        queryParams: { serviceId } 
      });
    }
  }

  contactSupport(): void {
    // Navigate to support page or open contact modal
    this.router.navigate(['/support']);
  }
}
