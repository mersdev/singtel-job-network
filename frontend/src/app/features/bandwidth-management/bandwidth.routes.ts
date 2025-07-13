import { Routes } from '@angular/router';

export const bandwidthRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./bandwidth-management.component').then(m => m.BandwidthManagementComponent)
  }
];
