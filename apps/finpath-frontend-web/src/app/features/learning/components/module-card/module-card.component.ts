import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { LearningModuleSummary } from '@models/learning.models';

@Component({
  selector: 'app-module-card',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslateModule],
  templateUrl: './module-card.component.html',
  styleUrls: ['./module-card.component.css']
})
export class ModuleCardComponent {
  @Input({ required: true }) module!: LearningModuleSummary;

  getCategoryColor(): string {
    const colors: Record<string, string> = {
      basics: 'bg-blue-500/20 text-blue-400',
      budgeting: 'bg-emerald-500/20 text-emerald-400',
      investing: 'bg-purple-500/20 text-purple-400',
      taxes: 'bg-yellow-500/20 text-yellow-400',
      retirement: 'bg-indigo-500/20 text-indigo-400',
      debt_management: 'bg-red-500/20 text-red-400',
      advanced: 'bg-pink-500/20 text-pink-400'
    };
    return colors[this.module.category] || 'bg-slate-500/20 text-slate-400';
  }

  getStatusBadgeClass(): string {
    const classes: Record<string, string> = {
      not_started: 'bg-slate-500/20 text-slate-400',
      in_progress: 'bg-yellow-500/20 text-yellow-400',
      completed: 'bg-emerald-500/20 text-emerald-400'
    };
    return classes[this.module.userStatus] || classes['not_started'];
  }

  getDifficultyStars(): number[] {
    return Array(5).fill(0).map((_, i) => i < this.module.difficultyLevel ? 1 : 0);
  }
}
