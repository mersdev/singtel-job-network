import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent, SidebarComponent],
  template: `
    <div class="min-h-screen bg-gray-50 lg:grid lg:grid-cols-[256px_1fr]">
      <!-- Sidebar -->
      <app-sidebar [isOpen]="sidebarOpen()" (sidebarClose)="closeSidebar()" />

      <!-- Main content area -->
      <div class="flex flex-col min-h-screen">
        <!-- Header -->
        <app-header (toggleSidebar)="toggleSidebar()" />

        <!-- Page content -->
        <main class="flex-1 overflow-auto">
          <div class="py-6">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
              <router-outlet />
            </div>
          </div>
        </main>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      min-height: 100vh;
    }
  `]
})
export class MainLayoutComponent {
  sidebarOpen = signal(false);

  toggleSidebar(): void {
    this.sidebarOpen.update(open => !open);
  }

  closeSidebar(): void {
    this.sidebarOpen.set(false);
  }
}
