import { Routes } from '@angular/router';
import { AUTH_ROUTES } from '@features/auth/routes';

export const APP_ROUTES: Routes = [
  ...AUTH_ROUTES,
  { path: 'dashboard',
    loadChildren: () => import('@features/dashboard/routes').then(m => m.DASHBOARD_ROUTES),
    title: 'Dashboard',
    data: { icon: 'dashboard', breadcrumb: 'Dashboard' }
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
];
