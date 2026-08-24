import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AnalyticsDashboardComponent } from './analytics-dashboard.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { AnalyticsService } from '../../core/services/analytics.service';
import { of } from 'rxjs';

describe('AnalyticsDashboardComponent', () => {
  let component: AnalyticsDashboardComponent;
  let fixture: ComponentFixture<AnalyticsDashboardComponent>;
  let analyticsService: AnalyticsService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnalyticsDashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideAnimationsAsync(),
        provideCharts(withDefaultRegisterables()),
        AnalyticsService
      ]
    }).compileComponents();

    analyticsService = TestBed.inject(AnalyticsService);
    spyOn(analyticsService, 'getSummary').and.returnValue(of({
      totalHeadcount: 10000,
      totalAnnualPayroll: 100000000,
      averageAnnualSalary: 100000,
      medianAnnualSalary: 95000,
      minSalary: 25000,
      maxSalary: 350000
    }));
    spyOn(analyticsService, 'getSalaryByCountry').and.returnValue(of([]));
    spyOn(analyticsService, 'getSalaryByDepartment').and.returnValue(of([]));
    spyOn(analyticsService, 'getSalaryByJobLevel').and.returnValue(of([]));
    spyOn(analyticsService, 'getSalaryDistribution').and.returnValue(of([]));

    fixture = TestBed.createComponent(AnalyticsDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load summary data', () => {
    expect(component).toBeTruthy();
    expect(component.summary).toBeTruthy();
    expect(component.summary?.totalHeadcount).toBe(10000);
  });

  it('should format currency accurately', () => {
    expect(component.formatCurrency(100000)).toContain('$100,000');
  });

  it('should reload data when inactive checkbox is toggled', () => {
    const spy = spyOn(component as any, 'loadAllData').and.callThrough();
    component.includeInactive = true;
    component.onToggleInactive();
    expect(spy).toHaveBeenCalled();
  });
});
