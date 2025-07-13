import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface OrderTemplate {
  id: string;
  name: string;
  description: string;
  serviceType: string;
  category: string;
  isDefault: boolean;
  usageCount: number;
  lastUsed?: string;
  createdAt: string;
}

@Component({
  selector: 'app-templates',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div class="flex justify-between items-start">
          <div>
            <h1 class="text-2xl font-bold text-gray-900 mb-2">Order Templates</h1>
            <p class="text-gray-600">
              Save time by creating reusable order templates for common service requests.
            </p>
          </div>
          <button
            (click)="createTemplate()"
            class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-singtel-blue hover:bg-blue-700"
          >
            <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            Create Template
          </button>
        </div>
      </div>

      <!-- Filter Tabs -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="border-b border-gray-200">
          <nav class="-mb-px flex space-x-8 px-6">
            @for (tab of filterTabs(); track tab.key) {
              <button
                (click)="setActiveFilter(tab.key)"
                [class]="activeFilter() === tab.key ? 
                  'border-singtel-blue text-singtel-blue whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm' :
                  'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm'"
              >
                {{ tab.label }}
                @if (tab.count > 0) {
                  <span class="ml-2 bg-gray-100 text-gray-900 py-0.5 px-2.5 rounded-full text-xs font-medium">
                    {{ tab.count }}
                  </span>
                }
              </button>
            }
          </nav>
        </div>

        <!-- Templates Grid -->
        <div class="p-6">
          @if (filteredTemplates().length === 0) {
            <div class="text-center py-12">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <h3 class="mt-2 text-sm font-medium text-gray-900">No templates found</h3>
              <p class="mt-1 text-sm text-gray-500">
                @if (activeFilter() === 'all') {
                  Get started by creating your first order template.
                } @else {
                  No templates found for the selected filter.
                }
              </p>
              @if (activeFilter() === 'all') {
                <div class="mt-6">
                  <button
                    (click)="createTemplate()"
                    class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-singtel-blue hover:bg-blue-700"
                  >
                    <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                    Create Template
                  </button>
                </div>
              }
            </div>
          } @else {
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              @for (template of filteredTemplates(); track template.id) {
                <div class="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow">
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <div class="flex items-center">
                        <h3 class="text-lg font-medium text-gray-900">{{ template.name }}</h3>
                        @if (template.isDefault) {
                          <span class="ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                            Default
                          </span>
                        }
                      </div>
                      <p class="mt-1 text-sm text-gray-600">{{ template.description }}</p>
                      
                      <div class="mt-4 space-y-2">
                        <div class="flex items-center text-sm text-gray-500">
                          <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                          </svg>
                          {{ template.category }}
                        </div>
                        <div class="flex items-center text-sm text-gray-500">
                          <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                          </svg>
                          {{ template.serviceType }}
                        </div>
                        <div class="flex items-center text-sm text-gray-500">
                          <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                          </svg>
                          Used {{ template.usageCount }} times
                        </div>
                        @if (template.lastUsed) {
                          <div class="flex items-center text-sm text-gray-500">
                            <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            Last used {{ formatDate(template.lastUsed) }}
                          </div>
                        }
                      </div>
                    </div>
                    
                    <div class="ml-4 flex-shrink-0">
                      <div class="relative">
                        <button
                          (click)="toggleTemplateMenu(template.id)"
                          class="p-2 rounded-full text-gray-400 hover:text-gray-500 hover:bg-gray-100"
                        >
                          <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                          </svg>
                        </button>
                        
                        @if (activeTemplateMenu() === template.id) {
                          <div class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10 border border-gray-200">
                            <button
                              (click)="useTemplate(template)"
                              class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                            >
                              Use Template
                            </button>
                            <button
                              (click)="editTemplate(template)"
                              class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                            >
                              Edit Template
                            </button>
                            <button
                              (click)="duplicateTemplate(template)"
                              class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                            >
                              Duplicate
                            </button>
                            @if (!template.isDefault) {
                              <div class="border-t border-gray-100"></div>
                              <button
                                (click)="deleteTemplate(template)"
                                class="block w-full text-left px-4 py-2 text-sm text-red-700 hover:bg-red-50"
                              >
                                Delete
                              </button>
                            }
                          </div>
                        }
                      </div>
                    </div>
                  </div>
                  
                  <div class="mt-6 flex space-x-3">
                    <button
                      (click)="useTemplate(template)"
                      class="flex-1 bg-singtel-blue text-white text-sm font-medium py-2 px-4 rounded-md hover:bg-blue-700"
                    >
                      Use Template
                    </button>
                    <button
                      (click)="editTemplate(template)"
                      class="flex-1 bg-white text-gray-700 text-sm font-medium py-2 px-4 rounded-md border border-gray-300 hover:bg-gray-50"
                    >
                      Edit
                    </button>
                  </div>
                </div>
              }
            </div>
          }
        </div>
      </div>
    </div>
  `
})
export class TemplatesComponent {
  activeFilter = signal('all');
  activeTemplateMenu = signal<string | null>(null);

  // Mock data - in a real app, this would come from a service
  templates = signal<OrderTemplate[]>([
    {
      id: '1',
      name: 'Standard Internet Connection',
      description: 'Basic internet connection setup for small offices',
      serviceType: 'Internet',
      category: 'Connectivity',
      isDefault: true,
      usageCount: 15,
      lastUsed: '2024-01-10T10:30:00Z',
      createdAt: '2023-12-01T09:00:00Z'
    },
    {
      id: '2',
      name: 'High-Speed Fiber Setup',
      description: 'Premium fiber connection for high-bandwidth requirements',
      serviceType: 'Fiber',
      category: 'Connectivity',
      isDefault: false,
      usageCount: 8,
      lastUsed: '2024-01-08T14:15:00Z',
      createdAt: '2023-12-15T11:30:00Z'
    },
    {
      id: '3',
      name: 'VPN Configuration',
      description: 'Secure VPN setup for remote access',
      serviceType: 'VPN',
      category: 'Security',
      isDefault: false,
      usageCount: 12,
      lastUsed: '2024-01-05T16:45:00Z',
      createdAt: '2023-11-20T08:15:00Z'
    }
  ]);

  filterTabs = signal([
    { key: 'all', label: 'All Templates', count: this.templates().length },
    { key: 'connectivity', label: 'Connectivity', count: this.templates().filter(t => t.category === 'Connectivity').length },
    { key: 'security', label: 'Security', count: this.templates().filter(t => t.category === 'Security').length },
    { key: 'recent', label: 'Recently Used', count: this.templates().filter(t => t.lastUsed).length }
  ]);

  filteredTemplates = signal<OrderTemplate[]>(this.templates());

  setActiveFilter(filter: string): void {
    this.activeFilter.set(filter);
    this.activeTemplateMenu.set(null);
    
    let filtered: OrderTemplate[] = [];
    switch (filter) {
      case 'connectivity':
        filtered = this.templates().filter(t => t.category === 'Connectivity');
        break;
      case 'security':
        filtered = this.templates().filter(t => t.category === 'Security');
        break;
      case 'recent':
        filtered = this.templates()
          .filter(t => t.lastUsed)
          .sort((a, b) => new Date(b.lastUsed!).getTime() - new Date(a.lastUsed!).getTime());
        break;
      default:
        filtered = this.templates();
    }
    
    this.filteredTemplates.set(filtered);
  }

  toggleTemplateMenu(templateId: string): void {
    this.activeTemplateMenu.update(current => 
      current === templateId ? null : templateId
    );
  }

  createTemplate(): void {
    // Navigate to template creation form
    alert('Template creation will be available soon!');
  }

  useTemplate(template: OrderTemplate): void {
    // Navigate to order creation with template pre-filled
    alert(`Using template: ${template.name}`);
  }

  editTemplate(template: OrderTemplate): void {
    // Navigate to template edit form
    alert(`Editing template: ${template.name}`);
  }

  duplicateTemplate(template: OrderTemplate): void {
    // Create a copy of the template
    alert(`Duplicating template: ${template.name}`);
  }

  deleteTemplate(template: OrderTemplate): void {
    if (confirm(`Are you sure you want to delete the template "${template.name}"?`)) {
      // Remove template from list
      const updated = this.templates().filter(t => t.id !== template.id);
      this.templates.set(updated);
      this.setActiveFilter(this.activeFilter()); // Refresh filtered list
      this.activeTemplateMenu.set(null);
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) {
      return 'yesterday';
    } else if (diffDays < 7) {
      return `${diffDays} days ago`;
    } else {
      return date.toLocaleDateString();
    }
  }
}
