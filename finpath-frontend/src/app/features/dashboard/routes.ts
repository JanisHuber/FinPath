import { Routes } from '@angular/router';
import { DashboardPage } from '../pages/dashboard-page/dashboard-page';

export const DASHBOARD_ROUTES: Routes = [
  { path: '/dashboard', component: DashboardPage, title: 'Dashboard' },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
];
