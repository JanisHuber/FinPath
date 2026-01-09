import { Routes } from '@angular/router';
import { DashboardPage } from './dashboard.page';
import { authGuard } from '@core/guards/auth.guard';

export const DASHBOARD_ROUTES: Routes = [
  { path: '', component: DashboardPage, title: 'Dashboard', canActivate: [authGuard] },
];
