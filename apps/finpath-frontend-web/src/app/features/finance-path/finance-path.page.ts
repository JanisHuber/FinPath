import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FinancialGoalService } from '@core/services/financial-goal.service';
import { FinancialGoal, GoalStatus, GoalCategory } from '@models/financial-goal.models';
import { GoalCardComponent } from './components/goal-card/goal-card.component';
import { GoalFormModalComponent } from './components/goal-form-modal/goal-form-modal.component';

@Component({
  selector: 'app-finance-path-page',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    GoalCardComponent,
    GoalFormModalComponent
  ],
  templateUrl: './finance-path.page.html',
  styleUrls: ['./finance-path.page.css']
})
export class FinancePathPage implements OnInit {
  goals: FinancialGoal[] = [];
  filteredGoals: FinancialGoal[] = [];
  isLoading = true;
  error: string | null = null;

  showGoalModal = false;
  selectedGoal: FinancialGoal | null = null;

  activeFilter: GoalStatus | 'all' = 'all';
  categoryFilter: GoalCategory | 'all' = 'all';

  private destroyRef = inject(DestroyRef);
  private goalService = inject(FinancialGoalService);

  ngOnInit(): void {
    this.loadGoals();
  }

  loadGoals(): void {
    this.isLoading = true;
    this.error = null;

    const status = this.activeFilter === 'all' ? undefined : this.activeFilter;
    const category = this.categoryFilter === 'all' ? undefined : this.categoryFilter;

    this.goalService.getGoals(status, category)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (goals) => {
          this.goals = goals;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading goals:', error);
          this.error = 'Failed to load goals';
          this.isLoading = false;
        }
      });
  }

  applyFilters(): void {
    this.filteredGoals = this.goals.filter(goal => {
      const statusMatch = this.activeFilter === 'all' || goal.status === this.activeFilter;
      const categoryMatch = this.categoryFilter === 'all' || goal.category === this.categoryFilter;
      return statusMatch && categoryMatch;
    });
  }

  setStatusFilter(status: GoalStatus | 'all'): void {
    this.activeFilter = status;
    this.applyFilters();
  }

  setCategoryFilter(category: GoalCategory | 'all'): void {
    this.categoryFilter = category;
    this.applyFilters();
  }

  openCreateModal(): void {
    this.selectedGoal = null;
    this.showGoalModal = true;
  }

  openEditModal(goal: FinancialGoal): void {
    this.selectedGoal = goal;
    this.showGoalModal = true;
  }

  closeModal(): void {
    this.showGoalModal = false;
    this.selectedGoal = null;
  }

  onGoalSaved(): void {
    this.closeModal();
    this.loadGoals();
  }

  onGoalDeleted(goalId: string): void {
    this.goalService.deleteGoal(goalId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.loadGoals();
        },
        error: (error) => {
          console.error('Error deleting goal:', error);
        }
      });
  }

  getActiveGoalsCount(): number {
    return this.goals.filter(g => g.status === 'active').length;
  }

  getCompletedGoalsCount(): number {
    return this.goals.filter(g => g.status === 'completed').length;
  }

  getTotalProgress(): number {
    const activeGoals = this.goals.filter(g => g.status === 'active');
    if (activeGoals.length === 0) return 0;
    const totalProgress = activeGoals.reduce((sum, g) => sum + g.progressPercent, 0);
    return Math.round(totalProgress / activeGoals.length);
  }
}
