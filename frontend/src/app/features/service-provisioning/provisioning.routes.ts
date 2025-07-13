import { Routes } from '@angular/router';

export const provisioningRoutes: Routes = [
  {
    path: '',
    redirectTo: 'new',
    pathMatch: 'full'
  },
  {
    path: 'new',
    loadComponent: () => import('./order-creation/order-creation.component').then(m => m.OrderCreationComponent)
  },
  {
    path: 'history',
    loadComponent: () => import('./order-history/order-history.component').then(m => m.OrderHistoryComponent)
  },
  {
    path: 'templates',
    loadComponent: () => import('./templates/templates.component').then(m => m.TemplatesComponent)
  }
];
