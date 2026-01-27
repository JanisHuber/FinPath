export type GoalStatus = 'active' | 'completed' | 'paused' | 'cancelled';

export type GoalCategory =
  | 'savings'
  | 'investment'
  | 'debt_payoff'
  | 'emergency_fund'
  | 'retirement'
  | 'education'
  | 'vacation'
  | 'other';

export interface Milestone {
  id: string;
  name: string;
  description?: string;
  targetAmount: number;
  targetDate?: string;
  isAchieved: boolean;
  achievedAt?: string;
  displayOrder: number;
}

export interface FinancialGoal {
  id: string;
  name: string;
  description?: string;
  targetAmount: number;
  currentAmount: number;
  deadline?: string;
  category: GoalCategory;
  status: GoalStatus;
  priority: number;
  icon?: string;
  color?: string;
  linkedAccountId?: string;
  progressPercent: number;
  estimatedCompletionDate?: string;
  milestones: Milestone[];
}

export interface CreateGoalRequest {
  name: string;
  description?: string;
  targetAmount: number;
  initialAmount?: number;
  deadline?: string;
  category: GoalCategory;
  priority?: number;
  icon?: string;
  color?: string;
  linkedAccountId?: string;
}

export interface UpdateGoalRequest {
  name?: string;
  description?: string;
  targetAmount?: number;
  currentAmount?: number;
  deadline?: string;
  category?: GoalCategory;
  status?: GoalStatus;
  priority?: number;
  icon?: string;
  color?: string;
  linkedAccountId?: string;
}

export interface CreateMilestoneRequest {
  name: string;
  description?: string;
  targetAmount: number;
  targetDate?: string;
  displayOrder?: number;
}

export interface UpdateMilestoneRequest {
  name?: string;
  description?: string;
  targetAmount?: number;
  targetDate?: string;
  isAchieved?: boolean;
  displayOrder?: number;
}

export interface ContributionRequest {
  amount: number;
}
