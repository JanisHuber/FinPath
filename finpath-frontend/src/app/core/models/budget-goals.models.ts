export interface BudgetGoals {
  goals: BudgetGoal[];
}

export interface BudgetGoal {
  id: number;
  name: string;
  targetAmount: number;
  currentAmount: number;
  dueDate?: string;
}