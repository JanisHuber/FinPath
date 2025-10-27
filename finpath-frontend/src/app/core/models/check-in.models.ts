export interface CheckIn {
  id: number;
  userUid: string;
  status: 'Good' | 'Average' | 'Bad';
  recommendations: Recommendation[];
  financialScore: FinancialScore;
}

export interface Recommendation {
  text: string;
  type: 'Finance' | 'Learning';
  priority: 'High' | 'Medium' | 'Low';
  link?: string;
}

export interface FinancialScore {
  score: number;
  contextTip: string;
}
