import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-bandwidth-management',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-4">Bandwidth Management</h1>
        <p class="text-gray-600">
          Dynamically adjust bandwidth allocation for your services in real-time.
        </p>
      </div>
      
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">MPLS Primary Link</h2>
          <div class="space-y-4">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Current: 500 Mbps</span>
              <span class="text-gray-600">Target: 750 Mbps</span>
            </div>
            
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div class="bg-blue-600 h-2 rounded-full" style="width: 75%"></div>
            </div>
            
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-600">Estimated cost: $750/month</span>
              <button class="btn-primary">Apply Changes</button>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Internet Backup</h2>
          <div class="space-y-4">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Current: 200 Mbps</span>
              <span class="text-gray-600">Target: 200 Mbps</span>
            </div>
            
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div class="bg-green-600 h-2 rounded-full" style="width: 40%"></div>
            </div>
            
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-600">Estimated cost: $200/month</span>
              <button class="btn-secondary" disabled>No Changes</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class BandwidthManagementComponent {}
