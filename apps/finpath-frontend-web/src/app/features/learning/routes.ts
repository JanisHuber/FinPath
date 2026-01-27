import { Routes } from '@angular/router';
import { LearningPage } from './learning.page';
import { ModuleDetailPage } from './module-detail/module-detail.page';

export const LEARNING_ROUTES: Routes = [
  {
    path: '',
    component: LearningPage
  },
  {
    path: ':id',
    component: ModuleDetailPage
  }
];
