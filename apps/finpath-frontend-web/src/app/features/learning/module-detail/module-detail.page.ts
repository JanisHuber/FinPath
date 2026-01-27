import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { LearningService } from '@core/services/learning.service';
import { LearningModule } from '@models/learning.models';

@Component({
  selector: 'app-module-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslateModule],
  templateUrl: './module-detail.page.html',
  styleUrls: ['./module-detail.page.css']
})
export class ModuleDetailPage implements OnInit {
  module: LearningModule | null = null;
  isLoading = true;
  error: string | null = null;
  isCompleting = false;

  private destroyRef = inject(DestroyRef);
  private learningService = inject(LearningService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  ngOnInit(): void {
    const moduleId = this.route.snapshot.paramMap.get('id');
    if (moduleId) {
      this.loadModule(moduleId);
    } else {
      this.router.navigate(['/learning']);
    }
  }

  loadModule(id: string): void {
    this.isLoading = true;
    this.error = null;

    this.learningService.getModule(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (module) => {
          this.module = module;
          this.isLoading = false;

          // Auto-start if not started
          if (!module.userProgress || module.userProgress.status === 'not_started') {
            this.startModule();
          }
        },
        error: (error) => {
          console.error('Error loading module:', error);
          this.error = 'Failed to load module';
          this.isLoading = false;
        }
      });
  }

  startModule(): void {
    if (!this.module) return;

    this.learningService.startModule(this.module.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (progress) => {
          if (this.module) {
            this.module.userProgress = progress;
          }
        },
        error: (error) => {
          console.error('Error starting module:', error);
        }
      });
  }

  markComplete(): void {
    if (!this.module || this.isCompleting) return;

    this.isCompleting = true;

    this.learningService.completeModule(this.module.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (progress) => {
          if (this.module) {
            this.module.userProgress = progress;
          }
          this.isCompleting = false;
        },
        error: (error) => {
          console.error('Error completing module:', error);
          this.isCompleting = false;
        }
      });
  }

  getCategoryColor(): string {
    if (!this.module) return 'bg-slate-500/20 text-slate-400';
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

  getDifficultyStars(): number[] {
    if (!this.module) return [];
    return Array(5).fill(0).map((_, i) => i < this.module!.difficultyLevel ? 1 : 0);
  }

  isCompleted(): boolean {
    return this.module?.userProgress?.status === 'completed';
  }
}
