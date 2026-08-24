import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmployeeDetailComponent } from './employee-detail.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';
import { of } from 'rxjs';
import { Country, Currency, Department, EmployeeStatus, JobLevel } from '../../../shared/components/models/enums';

describe('EmployeeDetailComponent', () => {
  let component: EmployeeDetailComponent;
  let fixture: ComponentFixture<EmployeeDetailComponent>;
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
      imports: [EmployeeDetailComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideAnimationsAsync(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '123e4567-e89b-12d3-a456-426614174000'
              }
            }
          }
        },
        EmployeeService
      ]
    }).compileComponents();

    employeeService = TestBed.inject(EmployeeService);
    spyOn(employeeService, 'getEmployeeById').and.returnValue(of(mockEmployee));

    fixture = TestBed.createComponent(EmployeeDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load and display employee details', () => {
    expect(component).toBeTruthy();
    expect(component.employee).toEqual(mockEmployee);
    expect(component.employee?.fullName).toBe('Jane Doe');
  });

  it('should format currency values', () => {
    expect(component.formatSalary(65000, 'USD')).toContain('$65,000');
  });
});
