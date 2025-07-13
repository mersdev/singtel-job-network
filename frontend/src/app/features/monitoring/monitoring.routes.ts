import { Routes } from '@angular/router';

export const monitoringRoutes: Routes = [
  {
    path: '',
    redirectTo: 'status',
    pathMatch: 'full'
  },
  {
    path: 'status',
    loadComponent: () => import('./monitoring.component').then(m => m.MonitoringComponent)
  }
];
