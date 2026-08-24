import { Country, Currency, Department, EmployeeStatus, JobLevel } from './enums';

export interface EmployeeResponse {
  id: string;
  employeeId: string;
  fullName: string;
  email: string;
  department: Department;
  jobTitle: string;
  jobLevel: JobLevel;
  country: Country;
  currency: Currency;
  annualSalary: number;
  annualSalaryUsd: number;
  status: EmployeeStatus;
  dateOfJoining: string;      // ISO date (YYYY-MM-DD)
  createdAt: string;          // ISO timestamp
  updatedAt: string;
}

export interface EmployeeCreateRequest {
  employeeId: string;
  fullName: string;
  email: string;
  department: Department;
  jobTitle: string;
  jobLevel: JobLevel;
  country: Country;
  currency: Currency;
  annualSalary: number;
  dateOfJoining: string;
}

export interface EmployeeUpdateRequest {
  fullName: string;
  email: string;
  department: Department;
  jobTitle: string;
  jobLevel: JobLevel;
  country: Country;
  currency: Currency;
  annualSalary: number;
  dateOfJoining: string;
  status: EmployeeStatus;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface EmployeeSearchParams {
  search?: string;
  country?: Country;
  department?: Department;
  jobLevel?: JobLevel;
  status?: EmployeeStatus;
  page?: number;
  size?: number;
  sort?: string;
}