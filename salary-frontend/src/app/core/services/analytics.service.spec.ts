import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AnalyticsService } from './analytics.service';
import { environment } from '../../../enviroment/environment';
import { AnalyticsSummaryResponse, BreakdownItemResponse, SalaryDistributionItemResponse } from '../../shared/components/models/analytics.model';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AnalyticsService
      ]
    });
    service = TestBed.inject(AnalyticsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch summary metrics', () => {
    const mockSummary: AnalyticsSummaryResponse = {
      totalHeadcount: 10000,
      totalAnnualPayroll: 100000000,
      averageAnnualSalary: 100000,
      medianAnnualSalary: 95000,
      minSalary: 25000,
      maxSalary: 350000
    };

    service.getSummary(false).subscribe((res) => {
      expect(res).toEqual(mockSummary);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/analytics/summary?includeInactive=false`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSummary);
  });

  it('should fetch salary by country', () => {
    const mockData: BreakdownItemResponse[] = [
      { key: 'UNITED_STATES', averageSalary: 120000, totalPayroll: 24000000, headcount: 200 }
    ];

    service.getSalaryByCountry(true).subscribe((res) => {
      expect(res).toEqual(mockData);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/analytics/by-country?includeInactive=true`);
    expect(req.request.method).toBe('GET');
    req.flush(mockData);
  });

  it('should fetch salary distribution', () => {
    const mockData: SalaryDistributionItemResponse[] = [
      { band: '0 - 30k', count: 2000, percentage: 20.0 }
    ];

    service.getSalaryDistribution(false).subscribe((res) => {
      expect(res).toEqual(mockData);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/analytics/salary-distribution?includeInactive=false`);
    expect(req.request.method).toBe('GET');
    req.flush(mockData);
  });
});
