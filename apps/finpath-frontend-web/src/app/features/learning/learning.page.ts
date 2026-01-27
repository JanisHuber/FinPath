import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { LearningService } from '@core/services/learning.service';
import { LearningModuleSummary, LearningStats, LearningCategory, LearningStatus } from '@models/learning.models';
import { ModuleCardComponent } from './components/module-card/module-card.component';

@Component({
  selector: 'app-learning-page',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslateModule, ModuleCardComponent],
  templateUrl: './learning.page.html',
  styleUrls: ['./learning.page.css']
})
export class LearningPage implements OnInit {
  modules: LearningModuleSummary[] = [];
  filteredModules: LearningModuleSummary[] = [];
  stats: LearningStats | null = null;
  isLoading = true;
  error: string | null = null;

  categoryFilter: LearningCategory | 'all' = 'all';
  statusFilter: LearningStatus | 'all' = 'all';

  categories: LearningCategory[] = [
    'basics',
    'budgeting',
    'investing',
    'taxes',
    'retirement',
    'debt_management',
    'advanced'
  ];

  private destroyRef = inject(DestroyRef);
  private learningService = inject(LearningService);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.error = null;

    // Load modules and stats in parallel
    this.learningService.getModules()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (modules) => {
          this.modules = modules;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading modules:', error);
          this.error = 'Failed to load learning modules';
          this.isLoading = false;
        }
      });

    this.learningService.getStats()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (stats) => {
          this.stats = stats;
        },
        error: (error) => {
          console.error('Error loading stats:', error);
        }
      });
  }

  applyFilters(): void {
    this.filteredModules = this.modules.filter(module => {
      const categoryMatch = this.categoryFilter === 'all' || module.category === this.categoryFilter;
      const statusMatch = this.statusFilter === 'all' || module.userStatus === this.statusFilter;
      return categoryMatch && statusMatch;
    });
  }

  setCategoryFilter(category: LearningCategory | 'all'): void {
    this.categoryFilter = category;
    this.applyFilters();
  }

  setStatusFilter(status: LearningStatus | 'all'): void {
    this.statusFilter = status;
    this.applyFilters();
  }

  getCompletionPercentage(): number {
    if (!this.stats || this.stats.totalModules === 0) return 0;
    return Math.round((this.stats.completedModules / this.stats.totalModules) * 100);
  }

  getModulesByCategory(category: LearningCategory): LearningModuleSummary[] {
    return this.filteredModules.filter(m => m.category === category);
  }
}
