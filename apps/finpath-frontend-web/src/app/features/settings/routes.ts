import { Routes } from '@angular/router';
import { SettingsLayout } from './ui/settings.layout';

export const SETTINGS_ROUTES: Routes = [
  {
    path: '',
    component: SettingsLayout,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'profile' },
      { path: 'profile', loadComponent: () => import('./pages/profile/profile.page').then(m => m.SettingsProfilePage), title: 'Profil • Einstellungen' },
    ],
  },
];