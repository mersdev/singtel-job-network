import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/auth/auth.guard';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        component: LoginComponent,
        canActivate: [guestGuard]
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'services',
        loadChildren: () => import('./features/service-catalog/service-catalog.routes').then(m => m.serviceCatalogRoutes)
      },
      {
        path: 'provisioning',
        loadChildren: () => import('./features/service-provisioning/provisioning.routes').then(m => m.provisioningRoutes)
      },
      {
        path: 'bandwidth',
        loadChildren: () => import('./features/bandwidth-management/bandwidth.routes').then(m => m.bandwidthRoutes)
      },
      {
        path: 'monitoring',
        loadChildren: () => import('./features/monitoring/monitoring.routes').then(m => m.monitoringRoutes)
      },
      {
        path: 'orders',
        loadChildren: () => import('./features/orders/orders.routes').then(m => m.ordersRoutes)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/user/profile/profile.component').then(m => m.ProfileComponent)
      },
      {
        path: 'settings',
        loadComponent: () => import('./features/user/settings/settings.component').then(m => m.SettingsComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
