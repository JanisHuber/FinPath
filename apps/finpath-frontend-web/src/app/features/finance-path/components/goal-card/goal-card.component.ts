import { Component, Input, Output, EventEmitter, inject, DestroyRef } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FinancialGoal, Milestone } from '@models/financial-goal.models';
import { FinancialGoalService } from '@core/services/financial-goal.service';

@Component({
  selector: 'app-goal-card',
  standalone: true,
  imports: [CommonModule, TranslateModule, DecimalPipe],
  templateUrl: './goal-card.component.html',
  styleUrls: ['./goal-card.component.css']
})
export class GoalCardComponent {
  @Input({ required: true }) goal!: FinancialGoal;
  @Output() edit = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();

  showDetails = false;
  showMilestones = false;

  private destroyRef = inject(DestroyRef);
  private goalService = inject(FinancialGoalService);

  toggleDetails(): void {
    this.showDetails = !this.showDetails;
  }

  toggleMilestones(): void {
    this.showMilestones = !this.showMilestones;
  }

  onEdit(event: Event): void {
    event.stopPropagation();
    this.edit.emit();
  }

  onDelete(event: Event): void {
    event.stopPropagation();
    if (confirm('Are you sure you want to delete this goal?')) {
      this.delete.emit();
    }
  }

  toggleMilestoneAchieved(milestone: Milestone, event: Event): void {
    event.stopPropagation();
    this.goalService.markMilestoneAchieved(this.goal.id, milestone.id, !milestone.isAchieved)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          const index = this.goal.milestones.findIndex(m => m.id === milestone.id);
          if (index !== -1) {
            this.goal.milestones[index] = updated;
          }
        },
        error: (error) => {
          console.error('Error updating milestone:', error);
        }
      });
  }

  getCategoryIcon(): string {
    const icons: Record<string, string> = {
      savings: 'M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z',
      investment: 'M13 7h8m0 0v8m0-8l-8 8-4-4-6 6',
      debt_payoff: 'M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z',
      emergency_fund: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z',
      retirement: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
      education: 'M12 14l9-5-9-5-9 5 9 5z M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z',
      vacation: 'M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
      other: 'M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z'
    };
    return icons[this.goal.category] || icons['other'];
  }

  getCategoryColor(): string {
    const colors: Record<string, string> = {
      savings: 'bg-blue-500/20 text-blue-400',
      investment: 'bg-emerald-500/20 text-emerald-400',
      debt_payoff: 'bg-red-500/20 text-red-400',
      emergency_fund: 'bg-yellow-500/20 text-yellow-400',
      retirement: 'bg-purple-500/20 text-purple-400',
      education: 'bg-indigo-500/20 text-indigo-400',
      vacation: 'bg-pink-500/20 text-pink-400',
      other: 'bg-slate-500/20 text-slate-400'
    };
    return colors[this.goal.category] || colors['other'];
  }

  getProgressColor(): string {
    if (this.goal.progressPercent >= 100) return 'bg-emerald-500';
    if (this.goal.progressPercent >= 70) return 'bg-emerald-400';
    if (this.goal.progressPercent >= 40) return 'bg-yellow-400';
    return 'bg-red-400';
  }

  getStatusBadgeClass(): string {
    const classes: Record<string, string> = {
      active: 'bg-emerald-500/20 text-emerald-400',
      completed: 'bg-blue-500/20 text-blue-400',
      paused: 'bg-yellow-500/20 text-yellow-400',
      cancelled: 'bg-slate-500/20 text-slate-400'
    };
    return classes[this.goal.status] || classes['active'];
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString();
  }

  getDaysRemaining(): number | null {
    if (!this.goal.deadline) return null;
    const deadline = new Date(this.goal.deadline);
    const today = new Date();
    const diff = deadline.getTime() - today.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  getAchievedMilestonesCount(): number {
    return this.goal.milestones.filter(m => m.isAchieved).length;
  }
}
