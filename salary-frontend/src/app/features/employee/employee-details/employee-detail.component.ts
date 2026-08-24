import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

import { EmployeeService } from '../../../core/services/employee.service';
import { EmployeeResponse } from '../../../shared/components/models/employee.model';
import { EmployeeStatus } from '../../../shared/components/models/enums';
import { CountryLabels, DepartmentLabels, JobLevelLabels } from '../../../shared/components/models/display-labels';

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule
  ],
  templateUrl: './employee-detail.component.html',
  styleUrl: './employee-detail.component.scss'
})
export class EmployeeDetailComponent implements OnInit {

  employee: EmployeeResponse | null = null;

  departmentLabels = DepartmentLabels;
  jobLevelLabels = JobLevelLabels;
  countryLabels = CountryLabels;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private employeeService: EmployeeService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadEmployee(id);
    }
  }

  private loadEmployee(id: string): void {
    this.employeeService.getEmployeeById(id).subscribe({
      next: (data) => {
        this.employee = data;
      }
    });
  }

  onEdit(): void {
    if (this.employee) {
      this.router.navigate(['/employees', this.employee.id, 'edit']);
    }
  }

  onDeactivate(): void {
    if (!this.employee || this.employee.status === EmployeeStatus.INACTIVE) {
      return;
    }

    if (confirm(`Are you sure you want to deactivate ${this.employee.fullName}?`)) {
      this.employeeService.deactivateEmployee(this.employee.id).subscribe({
        next: (updated) => {
          this.employee = updated;
        }
      });
    }
  }

  onBack(): void {
    this.router.navigate(['/employees']);
  }

  formatSalary(salary: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
      maximumFractionDigits: 0
    }).format(salary);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }
}