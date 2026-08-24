import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

import { EmployeeService } from '../../../core/services/employee.service';
import { EmployeeResponse, EmployeeSearchParams } from '../../../shared/components/models/employee.model';
import { Country, Department, EmployeeStatus, JobLevel } from '../../../shared/components/models/enums';
import { CountryLabels, DepartmentLabels, JobLevelLabels } from '../../../shared/components/models/display-labels';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatTooltipModule
  ],
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.scss'
})
export class EmployeeListComponent implements OnInit {

  displayedColumns: string[] = [
    'employeeId',
    'fullName',
    'department',
    'jobLevel',
    'country',
    'annualSalary',
    'status',
    'actions'
  ];

  employees: EmployeeResponse[] = [];
  totalElements = 0;
  pageSize = 20;
  pageIndex = 0;

  // Filters
  search = '';
  selectedCountry: Country | null = null;
  selectedDepartment: Department | null = null;
  selectedJobLevel: JobLevel | null = null;
  selectedStatus: EmployeeStatus | null = null;

  // Enums for dropdowns
  countries = Object.values(Country);
  departments = Object.values(Department);
  jobLevels = Object.values(JobLevel);
  statuses = Object.values(EmployeeStatus);

  // Labels
  countryLabels = CountryLabels;
  departmentLabels = DepartmentLabels;
  jobLevelLabels = JobLevelLabels;

  private searchSubject = new Subject<string>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private employeeService: EmployeeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEmployees();

    // Debounced search
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(() => {
      this.pageIndex = 0;
      this.loadEmployees();
    });
  }

  loadEmployees(): void {
    const params: EmployeeSearchParams = {
      search: this.search || undefined,
      country: this.selectedCountry || undefined,
      department: this.selectedDepartment || undefined,
      jobLevel: this.selectedJobLevel || undefined,
      status: this.selectedStatus || undefined,
      page: this.pageIndex,
      size: this.pageSize,
      sort: 'fullName,asc'
    };

    this.employeeService.searchEmployees(params).subscribe({
      next: (response) => {
        this.employees = response.content;
        this.totalElements = response.totalElements;
      }
    });
  }

  onSearchChange(value: string): void {
    this.search = value;
    this.searchSubject.next(value);
  }

  onFilterChange(): void {
    this.pageIndex = 0;
    this.loadEmployees();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadEmployees();
  }

  clearFilters(): void {
    this.search = '';
    this.selectedCountry = null;
    this.selectedDepartment = null;
    this.selectedJobLevel = null;
    this.selectedStatus = null;
    this.pageIndex = 0;
    this.loadEmployees();
  }

  viewEmployee(id: string): void {
    this.router.navigate(['/employees', id]);
  }

  editEmployee(id: string): void {
    this.router.navigate(['/employees', id, 'edit']);
  }

  deactivateEmployee(employee: EmployeeResponse): void {
    if (employee.status === EmployeeStatus.INACTIVE) {
      return;
    }

    if (confirm(`Are you sure you want to deactivate ${employee.fullName}?`)) {
      this.employeeService.deactivateEmployee(employee.id).subscribe({
        next: () => {
          this.loadEmployees();
        }
      });
    }
  }

  createEmployee(): void {
    this.router.navigate(['/employees/new']);
  }

  formatSalary(salary: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
      maximumFractionDigits: 0
    }).format(salary);
  }
}