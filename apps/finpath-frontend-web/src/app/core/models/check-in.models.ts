export interface CheckIn {
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
