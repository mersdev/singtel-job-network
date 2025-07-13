import { Routes } from '@angular/router';

export const serviceCatalogRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./service-catalog.component').then(m => m.ServiceCatalogComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./service-detail/service-detail.component').then(m => m.ServiceDetailComponent)
  }
];
