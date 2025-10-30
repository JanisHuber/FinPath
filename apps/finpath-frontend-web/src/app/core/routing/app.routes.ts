import { Routes } from '@angular/router';


export const APP_ROUTES: Routes = [
  { path: '/dashboard', 
    loadChildren: () => import('@app/features/dashboard/routes').then(m => m.DASHBOARD_ROUTES),
    title: 'Dashboard',
    data: { icon: 'dashboard', breadcrumb: 'Dashboard' }
  },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
];
