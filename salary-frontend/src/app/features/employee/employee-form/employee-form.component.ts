import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

import { EmployeeService } from '../../../core/services/employee.service';
import { Country, Currency, Department, EmployeeStatus, JobLevel } from '../../../shared/components/models/enums';
import { CountryLabels, DepartmentLabels, JobLevelLabels } from '../../../shared/components/models/display-labels';
import { EmployeeCreateRequest, EmployeeUpdateRequest } from '../../../shared/components/models/employee.model';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardModule,
    MatIconModule
  ],
  templateUrl: './employee-form.component.html',
  styleUrl: './employee-form.component.scss'
})
export class EmployeeFormComponent implements OnInit {

  form!: FormGroup;
  isEditMode = false;
  employeeId: string | null = null;
  pageTitle = 'Add Employee';

  // Dropdown options
  departments = Object.values(Department);
  jobLevels = Object.values(JobLevel);
  countries = Object.values(Country);
  currencies = Object.values(Currency);
  statuses = Object.values(EmployeeStatus);

  // Labels
  departmentLabels = DepartmentLabels;
  jobLevelLabels = JobLevelLabels;
  countryLabels = CountryLabels;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    this.employeeId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.employeeId && this.router.url.includes('edit');

    if (this.isEditMode && this.employeeId) {
      this.pageTitle = 'Edit Employee';
      this.loadEmployee(this.employeeId);
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      employeeId: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50),
        Validators.pattern(/^[A-Z0-9\-]+$/)
      ]],
      fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
      department: [null, Validators.required],
      jobTitle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
      jobLevel: [null, Validators.required],
      country: [null, Validators.required],
      currency: [null, Validators.required],
      annualSalary: [null, [Validators.required, Validators.min(1)]],
      dateOfJoining: [null, Validators.required],
      status: [EmployeeStatus.ACTIVE]
    });
  }

  private loadEmployee(id: string): void {
    this.employeeService.getEmployeeById(id).subscribe({
      next: (employee) => {
        this.form.patchValue({
          employeeId: employee.employeeId,
          fullName: employee.fullName,
          email: employee.email,
          department: employee.department,
          jobTitle: employee.jobTitle,
          jobLevel: employee.jobLevel,
          country: employee.country,
          currency: employee.currency,
          annualSalary: employee.annualSalary,
          dateOfJoining: employee.dateOfJoining,
          status: employee.status
        });

        // employeeId should not be editable in edit mode
        this.form.get('employeeId')?.disable();
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.getRawValue(); // getRawValue includes disabled fields

    if (this.isEditMode && this.employeeId) {
      const request: EmployeeUpdateRequest = {
        fullName: formValue.fullName,
        email: formValue.email,
        department: formValue.department,
        jobTitle: formValue.jobTitle,
        jobLevel: formValue.jobLevel,
        country: formValue.country,
        currency: formValue.currency,
        annualSalary: formValue.annualSalary,
        dateOfJoining: this.formatDate(formValue.dateOfJoining),
        status: formValue.status
      };

      this.employeeService.updateEmployee(this.employeeId, request).subscribe({
        next: () => this.router.navigate(['/employees'])
      });
    } else {
      const request: EmployeeCreateRequest = {
        employeeId: formValue.employeeId,
        fullName: formValue.fullName,
        email: formValue.email,
        department: formValue.department,
        jobTitle: formValue.jobTitle,
        jobLevel: formValue.jobLevel,
        country: formValue.country,
        currency: formValue.currency,
        annualSalary: formValue.annualSalary,
        dateOfJoining: this.formatDate(formValue.dateOfJoining)
      };

      this.employeeService.createEmployee(request).subscribe({
        next: () => this.router.navigate(['/employees'])
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/employees']);
  }

  private formatDate(date: Date | string): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toISOString().split('T')[0]; // YYYY-MM-DD
  }

  // Helper for template validation
  hasError(controlName: string, errorName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.hasError(errorName) && (control.dirty || control.touched));
  }
}