import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmployeeListComponent } from './employee-list.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';
import { of } from 'rxjs';
import { Country, Currency, Department, EmployeeStatus, JobLevel } from '../../../shared/components/models/enums';

describe('EmployeeListComponent', () => {
  let component: EmployeeListComponent;
  let fixture: ComponentFixture<EmployeeListComponent>;
  let employeeService: EmployeeService;

  const mockEmployee = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    employeeId: 'ACME-00001',
    fullName: 'Jane Doe',
    email: 'jane.doe@acme.com',
    department: Department.HUMAN_RESOURCES,
    jobTitle: 'HR Specialist',
    jobLevel: JobLevel.L2,
    country: Country.UNITED_STATES,
    currency: Currency.USD,
    annualSalary: 65000,
    annualSalaryUsd: 65000,
    status: EmployeeStatus.ACTIVE,
    dateOfJoining: '2023-05-01',
    createdAt: '2023-05-01T00:00:00Z',
    updatedAt: '2023-05-01T00:00:00Z'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeeListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideAnimationsAsync(),
        provideRouter([]),
        EmployeeService
      ]
    }).compileComponents();

    employeeService = TestBed.inject(EmployeeService);
    spyOn(employeeService, 'searchEmployees').and.returnValue(of({
      content: [mockEmployee],
      page: 0,
      size: 20,
      totalElements: 1,
      totalPages: 1,
      first: true,
      last: true
    }));

    fixture = TestBed.createComponent(EmployeeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load initial employees', () => {
    expect(component).toBeTruthy();
    expect(component.employees.length).toBe(1);
    expect(component.totalElements).toBe(1);
  });

  it('should clear all filters', () => {
    component.search = 'Jane';
    component.selectedCountry = Country.UNITED_STATES;
    component.selectedDepartment = Department.HUMAN_RESOURCES;
    component.selectedJobLevel = JobLevel.L2;
    component.selectedStatus = EmployeeStatus.ACTIVE;

    component.clearFilters();

    expect(component.search).toBe('');
    expect(component.selectedCountry).toBeNull();
    expect(component.selectedDepartment).toBeNull();
    expect(component.selectedJobLevel).toBeNull();
    expect(component.selectedStatus).toBeNull();
  });
});
