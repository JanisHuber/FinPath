import { Routes } from '@angular/router';
import { DashboardPage } from '@features/dashboard/pages/dashboard-page/dashboard-page';

export const APP_ROUTES: Routes = [
  { path: '', component: DashboardPage },
  { path: '**', redirectTo: '' },
];
