import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { EmployeeService } from './employee.service';
import { environment } from '../../../enviroment/environment';
import { EmployeeCreateRequest, EmployeeResponse, EmployeeUpdateRequest, PageResponse } from '../../shared/components/models/employee.model';
import { Country, Currency, Department, EmployeeStatus, JobLevel } from '../../shared/components/models/enums';

describe('EmployeeService', () => {
  let service: EmployeeService;
  let httpMock: HttpTestingController;

  const mockEmployee: EmployeeResponse = {
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

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        EmployeeService
      ]
    });
    service = TestBed.inject(EmployeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch employee by ID', () => {
    service.getEmployeeById(mockEmployee.id).subscribe((res) => {
      expect(res).toEqual(mockEmployee);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/employees/${mockEmployee.id}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockEmployee);
  });

  it('should create an employee', () => {
    const createReq: EmployeeCreateRequest = {
      employeeId: 'ACME-00001',
      fullName: 'Jane Doe',
      email: 'jane.doe@acme.com',
      department: Department.HUMAN_RESOURCES,
      jobTitle: 'HR Specialist',
      jobLevel: JobLevel.L2,
      country: Country.UNITED_STATES,
      currency: Currency.USD,
      annualSalary: 65000,
      dateOfJoining: '2023-05-01'
    };

    service.createEmployee(createReq).subscribe((res) => {
      expect(res).toEqual(mockEmployee);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/employees`);
    expect(req.request.method).toBe('POST');
    req.flush(mockEmployee);
  });

  it('should update an employee', () => {
    const updateReq: EmployeeUpdateRequest = {
      fullName: 'Jane Doe Updated',
      email: 'jane.doe@acme.com',
      department: Department.HUMAN_RESOURCES,
      jobTitle: 'Senior HR Specialist',
      jobLevel: JobLevel.L3,
      country: Country.UNITED_STATES,
      currency: Currency.USD,
      annualSalary: 75000,
      dateOfJoining: '2023-05-01',
      status: EmployeeStatus.ACTIVE
    };

    service.updateEmployee(mockEmployee.id, updateReq).subscribe((res) => {
      expect(res).toEqual(mockEmployee);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/employees/${mockEmployee.id}`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockEmployee);
  });

  it('should deactivate an employee', () => {
    service.deactivateEmployee(mockEmployee.id).subscribe((res) => {
      expect(res).toEqual(mockEmployee);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/employees/${mockEmployee.id}/deactivate`);
    expect(req.request.method).toBe('PATCH');
    req.flush(mockEmployee);
  });
});
