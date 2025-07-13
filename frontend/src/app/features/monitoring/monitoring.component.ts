import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-monitoring',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-4">Service Monitoring</h1>
        <p class="text-gray-600">
          Real-time monitoring and performance metrics for your network services.
        </p>
      </div>
      
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Service Status -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Service Status</h2>
          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-3 h-3 bg-green-500 rounded-full mr-3"></div>
                <div>
                  <p class="text-sm font-medium text-gray-900">MPLS Primary Link</p>
                  <p class="text-xs text-gray-500">Singapore DC1</p>
                </div>
              </div>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Active
              </span>
            </div>
            
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-3 h-3 bg-green-500 rounded-full mr-3"></div>
                <div>
                  <p class="text-sm font-medium text-gray-900">Internet Backup</p>
                  <p class="text-xs text-gray-500">Singapore DC2</p>
                </div>
              </div>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Active
              </span>
            </div>
            
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-3 h-3 bg-yellow-500 rounded-full mr-3"></div>
                <div>
                  <p class="text-sm font-medium text-gray-900">VPN Gateway</p>
                  <p class="text-xs text-gray-500">Malaysia KL</p>
                </div>
              </div>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                Warning
              </span>
            </div>
          </div>
        </div>
        
        <!-- Performance Metrics -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Performance Metrics</h2>
          <div class="space-y-4">
            <div>
              <div class="flex justify-between text-sm mb-1">
                <span class="text-gray-600">Bandwidth Utilization</span>
                <span class="font-medium">75%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-blue-600 h-2 rounded-full" style="width: 75%"></div>
              </div>
            </div>
            
            <div>
              <div class="flex justify-between text-sm mb-1">
                <span class="text-gray-600">Latency</span>
                <span class="font-medium">12ms</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-green-600 h-2 rounded-full" style="width: 90%"></div>
              </div>
            </div>
            
            <div>
              <div class="flex justify-between text-sm mb-1">
                <span class="text-gray-600">Packet Loss</span>
                <span class="font-medium">0.01%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-green-600 h-2 rounded-full" style="width: 99%"></div>
              </div>
            </div>
            
            <div>
              <div class="flex justify-between text-sm mb-1">
                <span class="text-gray-600">Uptime</span>
                <span class="font-medium">99.9%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-green-600 h-2 rounded-full" style="width: 99%"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Alerts -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-4">Recent Alerts</h2>
        <div class="space-y-3">
          <div class="flex items-start space-x-3 p-3 bg-yellow-50 border border-yellow-200 rounded-md">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="flex-1">
              <p class="text-sm font-medium text-yellow-800">High bandwidth utilization detected</p>
              <p class="text-xs text-yellow-700 mt-1">VPN Gateway - Malaysia KL is experiencing high traffic</p>
              <p class="text-xs text-yellow-600 mt-1">2 hours ago</p>
            </div>
          </div>
          
          <div class="flex items-start space-x-3 p-3 bg-green-50 border border-green-200 rounded-md">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-green-400" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="flex-1">
              <p class="text-sm font-medium text-green-800">Service restored</p>
              <p class="text-xs text-green-700 mt-1">MPLS Primary Link - Singapore DC1 is back online</p>
              <p class="text-xs text-green-600 mt-1">6 hours ago</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class MonitoringComponent {}
