import { Component, Input, Output, EventEmitter, OnInit, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FinancialGoal, GoalCategory, CreateGoalRequest, UpdateGoalRequest, CreateMilestoneRequest } from '@models/financial-goal.models';
import { FinancialGoalService } from '@core/services/financial-goal.service';

@Component({
  selector: 'app-goal-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslateModule],
  templateUrl: './goal-form-modal.component.html',
  styleUrls: ['./goal-form-modal.component.css']
})
export class GoalFormModalComponent implements OnInit {
  @Input() goal: FinancialGoal | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  form!: FormGroup;
  isSubmitting = false;
  error: string | null = null;

  categories: GoalCategory[] = [
    'savings',
    'investment',
    'debt_payoff',
    'emergency_fund',
    'retirement',
    'education',
    'vacation',
    'other'
  ];

  private fb = inject(FormBuilder);
  private destroyRef = inject(DestroyRef);
  private goalService = inject(FinancialGoalService);

  get isEditing(): boolean {
    return this.goal !== null;
  }

  get milestonesArray(): FormArray {
    return this.form.get('milestones') as FormArray;
  }

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.group({
      name: [this.goal?.name || '', [Validators.required, Validators.maxLength(100)]],
      description: [this.goal?.description || ''],
      targetAmount: [this.goal?.targetAmount || '', [Validators.required, Validators.min(0.01)]],
      currentAmount: [this.goal?.currentAmount || 0, [Validators.min(0)]],
      deadline: [this.goal?.deadline ? this.formatDateForInput(this.goal.deadline) : ''],
      category: [this.goal?.category || 'savings', Validators.required],
      priority: [this.goal?.priority || 1],
      icon: [this.goal?.icon || ''],
      color: [this.goal?.color || '#10B981'],
      milestones: this.fb.array([])
    });

    // Add existing milestones if editing
    if (this.goal?.milestones) {
      this.goal.milestones.forEach(milestone => {
        this.addMilestone(milestone.name, milestone.targetAmount, milestone.targetDate);
      });
    }
  }

  private formatDateForInput(dateString: string): string {
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
  }

  addMilestone(name = '', targetAmount: number | null = null, targetDate: string | undefined = undefined): void {
    const milestoneGroup = this.fb.group({
      name: [name, [Validators.required, Validators.maxLength(100)]],
      targetAmount: [targetAmount, [Validators.required, Validators.min(0.01)]],
      targetDate: [targetDate ? this.formatDateForInput(targetDate) : '']
    });
    this.milestonesArray.push(milestoneGroup);
  }

  removeMilestone(index: number): void {
    this.milestonesArray.removeAt(index);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.error = null;

    const formValue = this.form.value;

    if (this.isEditing && this.goal) {
      const request: UpdateGoalRequest = {
        name: formValue.name,
        description: formValue.description || undefined,
        targetAmount: formValue.targetAmount,
        currentAmount: formValue.currentAmount,
        deadline: formValue.deadline || undefined,
        category: formValue.category,
        priority: formValue.priority,
        icon: formValue.icon || undefined,
        color: formValue.color || undefined
      };

      this.goalService.updateGoal(this.goal.id, request)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            // Handle milestones for existing goal
            this.handleMilestones(this.goal!.id, formValue.milestones);
          },
          error: (error) => {
            console.error('Error updating goal:', error);
            this.error = 'Failed to update goal';
            this.isSubmitting = false;
          }
        });
    } else {
      const request: CreateGoalRequest = {
        name: formValue.name,
        description: formValue.description || undefined,
        targetAmount: formValue.targetAmount,
        initialAmount: formValue.currentAmount || undefined,
        deadline: formValue.deadline || undefined,
        category: formValue.category,
        priority: formValue.priority,
        icon: formValue.icon || undefined,
        color: formValue.color || undefined
      };

      this.goalService.createGoal(request)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (createdGoal) => {
            // Create milestones for new goal
            this.handleMilestones(createdGoal.id, formValue.milestones);
          },
          error: (error) => {
            console.error('Error creating goal:', error);
            this.error = 'Failed to create goal';
            this.isSubmitting = false;
          }
        });
    }
  }

  private handleMilestones(goalId: string, milestones: any[]): void {
    if (!milestones || milestones.length === 0) {
      this.isSubmitting = false;
      this.saved.emit();
      return;
    }

    // For simplicity, we just create all milestones (not updating existing ones)
    // A more complete implementation would diff and update
    const createPromises = milestones.map((m, index) => {
      const request: CreateMilestoneRequest = {
        name: m.name,
        targetAmount: m.targetAmount,
        targetDate: m.targetDate || undefined,
        displayOrder: index
      };
      return this.goalService.createMilestone(goalId, request).toPromise();
    });

    Promise.all(createPromises)
      .then(() => {
        this.isSubmitting = false;
        this.saved.emit();
      })
      .catch((error) => {
        console.error('Error creating milestones:', error);
        this.isSubmitting = false;
        this.saved.emit(); // Still emit saved since goal was created
      });
  }

  onClose(): void {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.onClose();
    }
  }
}
