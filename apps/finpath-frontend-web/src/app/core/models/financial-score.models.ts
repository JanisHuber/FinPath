export interface FinancialScore {
  score: number;
  contextTip: string;
  financialBreakdown: FinancialBreakdown;
}

export interface FinancialBreakdown {
  savings: number;
  savingsContextTip: string;
  emergencyFund: number;
  emergencyFundContextTip: string;
  investments: number;
  investmentsContextTip: string;
  debt: number;
  debtContextTip: string;
}
