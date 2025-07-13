import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ServiceCatalogService, ServiceCategory, ServiceSummary, ServiceSearchParams } from '../../core/api/service-catalog.service';

@Component({
  selector: 'app-service-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 data-cy="catalog-header" class="text-2xl font-bold text-gray-900 mb-4">Service Catalog</h1>
        <p data-cy="catalog-description" class="text-gray-600">
          Browse and order from our comprehensive catalog of network services.
        </p>
      </div>

      <!-- Search and Filters -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div class="space-y-4 mb-6">
          <!-- Search Row -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="search" class="block text-sm font-medium text-gray-700 mb-2">Search Services</label>
              <div class="flex space-x-2">
                <input
                  type="text"
                  id="search"
                  data-cy="search-input"
                  [(ngModel)]="searchQuery"
                  (input)="onSearchChange()"
                  placeholder="Search by name..."
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
          </div>

          <!-- Filters Row -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">

          <div>
            <label for="category" class="block text-sm font-medium text-gray-700 mb-2">Category</label>
            <select
              id="category"
              data-cy="category-filter"
              [(ngModel)]="selectedCategory"
              (change)="onFilterChange()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            >
              <option value="">All Categories</option>
              @for (category of categories(); track category.id) {
                <option [value]="category.id">{{ category.name }}</option>
              }
            </select>
          </div>

          <div>
            <label for="serviceType" class="block text-sm font-medium text-gray-700 mb-2">Service Type</label>
            <select
              id="serviceType"
              data-cy="service-type-filter"
              [(ngModel)]="selectedServiceType"
              (change)="onFilterChange()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            >
              <option value="">All Types</option>
              @for (type of serviceTypes(); track type) {
                <option [value]="type">{{ type }}</option>
              }
            </select>
          </div>

          <div>
            <label for="priceRange" class="block text-sm font-medium text-gray-700 mb-2">Price Range</label>
            <select
              id="priceRange"
              data-cy="price-range-filter"
              [(ngModel)]="selectedPriceRange"
              (change)="onFilterChange()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-singtel-blue focus:border-transparent"
            >
              <option value="">Any Price</option>
              <option value="0-200">$0 - $200</option>
              <option value="200-500">$200 - $500</option>
              <option value="500-1000">$500 - $1000</option>
              <option value="1000+">$1000+</option>
            </select>
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
      </div>

      <!-- Loading State -->
      @if (isLoading()) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <div data-cy="loading-spinner" class="animate-spin rounded-full h-12 w-12 border-b-2 border-singtel-blue mx-auto"></div>
          <p class="mt-4 text-gray-600">Loading services...</p>
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
              <h3 class="text-sm font-medium text-red-800">Error loading services</h3>
              <p data-cy="error-message" class="mt-1 text-sm text-red-700">{{ error() }}</p>
              <button
                data-cy="retry-button"
                (click)="loadServices()"
                class="mt-2 text-sm font-medium text-red-800 hover:text-red-900 underline"
              >
                Try again
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Services Grid -->
      @if (!isLoading() && !error() && services().length > 0) {
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-lg font-medium text-gray-900">
              Available Services ({{ totalServices() }} found)
            </h2>
            <div class="flex items-center space-x-2">
              <span class="text-sm text-gray-500">Sort by:</span>
              <select
                data-cy="sort-select"
                [(ngModel)]="sortBy"
                (change)="onSortChange()"
                class="text-sm border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-singtel-blue"
              >
                <option value="name">Name</option>
                <option value="price">Price</option>
                <option value="category">Category</option>
              </select>
            </div>
          </div>

          <div data-cy="services-grid" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            @for (service of services(); track service.id) {
              <div data-cy="service-card" class="border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow cursor-pointer"
                   (click)="viewServiceDetails(service.id)">
                <div class="flex justify-between items-start mb-3">
                  <h3 data-cy="service-name" class="font-medium text-gray-900 text-lg">{{ service.name }}</h3>
                  @if (service.isBandwidthAdjustable) {
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      Adjustable
                    </span>
                  }
                </div>

                <p data-cy="service-description" class="text-sm text-gray-600 mb-4 line-clamp-2">{{ service.description }}</p>

                <div class="space-y-2 mb-4">
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-500">Category:</span>
                    <span data-cy="service-category" class="font-medium">{{ service.categoryName }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-500">Type:</span>
                    <span class="font-medium">{{ service.serviceType }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-500">Setup Time:</span>
                    <span class="font-medium">{{ service.provisioningTimeHours }}h</span>
                  </div>
                </div>

                <div class="border-t pt-4">
                  <div class="flex justify-between items-center">
                    <div>
                      <span data-cy="service-price" class="text-lg font-bold text-singtel-blue">\${{ service.basePriceMonthly || service.monthlyPrice }}/month</span>
                      @if (service.setupFee > 0) {
                        <p class="text-xs text-gray-500">Setup: \${{ service.setupFee }}</p>
                      }
                    </div>
                    <button
                      data-cy="order-button"
                      (click)="orderService(service.id, $event)"
                      class="px-4 py-2 bg-singtel-blue text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors"
                    >
                      Order Now
                    </button>
                  </div>
                </div>
              </div>
            }
          </div>

          <!-- Pagination -->
          @if (totalPages() > 1) {
            <div data-cy="pagination" class="mt-8 flex justify-center">
              <nav class="flex items-center space-x-2">
                <button
                  (click)="goToPage(currentPage() - 1)"
                  [disabled]="currentPage() === 1"
                  class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
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
                    [class.text-gray-700]="page !== currentPage()"
                    class="px-3 py-2 text-sm font-medium border border-gray-300 rounded-md hover:bg-gray-50"
                  >
                    {{ page }}
                  </button>
                }

                <button
                  data-cy="next-page"
                  (click)="goToPage(currentPage() + 1)"
                  [disabled]="currentPage() === totalPages()"
                  class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Next
                </button>
              </nav>
            </div>
          }
        </div>
      }

      <!-- Empty State -->
      @if (!isLoading() && !error() && services().length === 0) {
        <div data-cy="empty-results" class="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">No services found</h3>
          <p class="mt-1 text-sm text-gray-500">Try adjusting your search criteria or filters.</p>
          <button
            (click)="clearFilters()"
            class="mt-4 px-4 py-2 bg-singtel-blue text-white text-sm font-medium rounded-md hover:bg-blue-700"
          >
            Clear Filters
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    .line-clamp-2 {
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  `]
})
export class ServiceCatalogComponent implements OnInit {
  private serviceCatalogService = inject(ServiceCatalogService);
  private router = inject(Router);

  // Signals for reactive state management
  services = signal<ServiceSummary[]>([]);
  categories = signal<ServiceCategory[]>([]);
  serviceTypes = signal<string[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);

  // Pagination
  currentPage = signal(1);
  totalPages = signal(1);
  totalServices = signal(0);
  pageSize = 12;

  // Search and filters
  searchQuery = '';
  selectedCategory = '';
  selectedServiceType = '';
  selectedPriceRange = '';
  sortBy = 'name';

  // Computed values
  visiblePages = computed(() => {
    const current = this.currentPage();
    const total = this.totalPages();
    const pages: number[] = [];

    const start = Math.max(1, current - 2);
    const end = Math.min(total, current + 2);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  });

  ngOnInit(): void {
    this.loadInitialData();
  }

  private async loadInitialData(): Promise<void> {
    try {
      this.isLoading.set(true);
      this.error.set(null);

      // Load categories and service types in parallel
      const [categories, serviceTypes] = await Promise.all([
        this.serviceCatalogService.getCategories().toPromise(),
        this.serviceCatalogService.getServiceTypes().toPromise()
      ]);

      this.categories.set(Array.isArray(categories) ? categories : []);
      this.serviceTypes.set(Array.isArray(serviceTypes) ? serviceTypes : []);

      // Load services
      await this.loadServices();
    } catch (error: any) {
      console.error('Error loading initial data:', error);
      this.error.set(error.message || 'Failed to load service catalog data');
    } finally {
      this.isLoading.set(false);
    }
  }

  async loadServices(): Promise<void> {
    try {
      this.isLoading.set(true);
      this.error.set(null);

      // If no filters are applied, use the simple getAllServices endpoint
      const hasFilters = this.searchQuery.trim() || this.selectedCategory ||
                        this.selectedServiceType || this.selectedPriceRange;

      if (!hasFilters) {
        // Use the simple services endpoint that returns an array
        const services = await this.serviceCatalogService.getAllServices().toPromise();

        if (Array.isArray(services)) {
          const sortedServices = this.sortServices(services);

          // Apply pagination manually for non-paginated endpoint
          const startIndex = (this.currentPage() - 1) * this.pageSize;
          const endIndex = startIndex + this.pageSize;
          const paginatedServices = sortedServices.slice(startIndex, endIndex);

          this.services.set(paginatedServices);
          this.totalServices.set(services.length);
          this.totalPages.set(Math.ceil(services.length / this.pageSize));
        } else {
          this.services.set([]);
          this.totalServices.set(0);
          this.totalPages.set(1);
        }
      } else {
        // Use search endpoint with filters
        const searchParams: ServiceSearchParams = {
          page: this.currentPage(),
          limit: this.pageSize
        };

        // Add search query
        if (this.searchQuery.trim()) {
          searchParams.name = this.searchQuery.trim();
        }

        // Add category filter
        if (this.selectedCategory) {
          searchParams.categoryId = this.selectedCategory;
        }

        // Add service type filter
        if (this.selectedServiceType) {
          searchParams.serviceType = this.selectedServiceType;
        }

        // Add price range filter
        if (this.selectedPriceRange) {
          const [min, max] = this.parsePriceRange(this.selectedPriceRange);
          if (min !== undefined) searchParams.minPrice = min;
          if (max !== undefined) searchParams.maxPrice = max;
        }

        const response = await this.serviceCatalogService.searchServices(searchParams).toPromise();

        if (response && response.data) {
          let services = Array.isArray(response.data) ? response.data : [];

          // Apply sorting
          services = this.sortServices(services);

          this.services.set(services);
          this.totalServices.set(response.pagination?.total || services.length);
          this.totalPages.set(response.pagination?.totalPages || 1);
        } else {
          this.services.set([]);
          this.totalServices.set(0);
          this.totalPages.set(1);
        }
      }
    } catch (error: any) {
      console.error('Error loading services:', error);
      this.error.set(error.message || 'Failed to load services');
      this.services.set([]);
      this.totalServices.set(0);
      this.totalPages.set(1);
    } finally {
      this.isLoading.set(false);
    }
  }

  private parsePriceRange(range: string): [number | undefined, number | undefined] {
    switch (range) {
      case '0-200':
        return [0, 200];
      case '200-500':
        return [200, 500];
      case '500-1000':
        return [500, 1000];
      case '1000+':
        return [1000, undefined];
      default:
        return [undefined, undefined];
    }
  }

  private sortServices(services: ServiceSummary[]): ServiceSummary[] {
    return [...services].sort((a, b) => {
      switch (this.sortBy) {
        case 'name':
          return a.name.localeCompare(b.name);
        case 'price':
          return (a.basePriceMonthly || a.monthlyPrice || 0) - (b.basePriceMonthly || b.monthlyPrice || 0);
        case 'category':
          return a.categoryName.localeCompare(b.categoryName);
        default:
          return 0;
      }
    });
  }

  onSearchChange(): void {
    this.currentPage.set(1);
    this.loadServices();
  }

  onFilterChange(): void {
    this.currentPage.set(1);
    this.loadServices();
  }

  onSortChange(): void {
    const sortedServices = this.sortServices(this.services());
    this.services.set(sortedServices);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
      this.loadServices();
    }
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.selectedCategory = '';
    this.selectedServiceType = '';
    this.selectedPriceRange = '';
    this.sortBy = 'name';
    this.currentPage.set(1);
    this.loadServices();
  }

  viewServiceDetails(serviceId: string): void {
    this.router.navigate(['/services', serviceId]);
  }

  orderService(serviceId: string, event: Event): void {
    event.stopPropagation(); // Prevent triggering viewServiceDetails
    this.router.navigate(['/provisioning/new'], {
      queryParams: { serviceId }
    });
  }
}
