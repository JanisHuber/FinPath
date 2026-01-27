export type LearningStatus = 'not_started' | 'in_progress' | 'completed';

export type LearningCategory =
  | 'basics'
  | 'budgeting'
  | 'investing'
  | 'taxes'
  | 'retirement'
  | 'debt_management'
  | 'advanced';

export interface LearningProgress {
  id: string;
  moduleId: string;
  status: LearningStatus;
  progressPercent: number;
  startedAt?: string;
  completedAt?: string;
  lastAccessedAt?: string;
  notes?: string;
}

export interface LearningModuleSummary {
  id: string;
  title: string;
  description?: string;
  summary?: string;
  category: LearningCategory;
  difficultyLevel: number;
  estimatedMinutes: number;
  tags: string[];
  userStatus: LearningStatus;
  userProgress: number;
}

export interface LearningModule {
  id: string;
  title: string;
  description?: string;
  content: string;
  summary?: string;
  category: LearningCategory;
  difficultyLevel: number;
  estimatedMinutes: number;
  displayOrder: number;
  tags: string[];
  userProgress?: LearningProgress;
}

export interface LearningStats {
  totalModules: number;
  completedModules: number;
  inProgressModules: number;
  averageProgress: number;
}

export interface UpdateProgressRequest {
  status?: LearningStatus;
  progressPercent?: number;
  notes?: string;
}
