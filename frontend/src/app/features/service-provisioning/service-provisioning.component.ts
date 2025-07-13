import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-service-provisioning',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-4">Service Provisioning</h1>
        <p class="text-gray-600">
          Order new services with our automated provisioning workflow.
        </p>
      </div>
      
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-4">Order Wizard</h2>
        <div class="space-y-4">
          <div class="border-l-4 border-blue-500 pl-4">
            <h3 class="font-medium text-gray-900">Step 1: Select Service</h3>
            <p class="text-sm text-gray-600">Choose the service you want to order</p>
          </div>
          
          <div class="border-l-4 border-gray-300 pl-4">
            <h3 class="font-medium text-gray-500">Step 2: Configure</h3>
            <p class="text-sm text-gray-500">Set up service parameters</p>
          </div>
          
          <div class="border-l-4 border-gray-300 pl-4">
            <h3 class="font-medium text-gray-500">Step 3: Review</h3>
            <p class="text-sm text-gray-500">Review and confirm your order</p>
          </div>
          
          <div class="border-l-4 border-gray-300 pl-4">
            <h3 class="font-medium text-gray-500">Step 4: Submit</h3>
            <p class="text-sm text-gray-500">Submit order for provisioning</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ServiceProvisioningComponent {}
