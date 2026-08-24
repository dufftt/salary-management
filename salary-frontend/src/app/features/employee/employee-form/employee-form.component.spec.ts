import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmployeeFormComponent } from './employee-form.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';
import { of } from 'rxjs';

describe('EmployeeFormComponent', () => {
  let component: EmployeeFormComponent;
  let fixture: ComponentFixture<EmployeeFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeeFormComponent],
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
                get: () => null
              }
            }
          }
        },
        EmployeeService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create in Add mode', () => {
    expect(component).toBeTruthy();
    expect(component.isEditMode).toBe(false);
    expect(component.pageTitle).toBe('Add Employee');
  });

  it('should validate required fields in form', () => {
    expect(component.form.valid).toBe(false);
    component.form.patchValue({
      employeeId: 'ACME-00001',
      fullName: 'John Doe',
      email: 'john.doe@acme.com',
      department: 'ENGINEERING',
      jobTitle: 'Developer',
      jobLevel: 'L3',
      country: 'UNITED_STATES',
      currency: 'USD',
      annualSalary: 100000,
      dateOfJoining: new Date()
    });
    expect(component.form.valid).toBe(true);
  });
});
