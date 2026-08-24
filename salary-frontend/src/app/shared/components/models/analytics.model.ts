export interface AnalyticsSummaryResponse {
  totalHeadcount: number;
  totalAnnualPayroll: number;
  averageAnnualSalary: number;
  medianAnnualSalary: number;
  minSalary: number;
  maxSalary: number;
}

export interface BreakdownItemResponse {
  key: string;
  averageSalary: number;
  totalSalary: number;
  headcount: number;
}

export interface SalaryDistributionItemResponse {
  band: string;
  count: number;
  percentage: number;
}