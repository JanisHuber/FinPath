import { Routes } from '@angular/router';
import { DashboardPage } from '@app/features/pages/dashboard-page/dashboard-page';

export const APP_ROUTES: Routes = [
  { path: '/dashboard', component: DashboardPage },
  { path: '/forbidden', component: ForbiddenPage },
  { path: '/login', component: LoginPage },
  { path: '/settings', component: SettingsPage },
  { path: '/profile', component: ProfilePage },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
];
