import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../enviroment/environment';
import {
  AnalyticsSummaryResponse,
  BreakdownItemResponse,
  SalaryDistributionItemResponse
} from '../../shared/components/models/analytics.model';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  private readonly baseUrl = `${environment.apiUrl}/analytics`;

  constructor(private http: HttpClient) {}

  getSummary(includeInactive: boolean = false): Observable<AnalyticsSummaryResponse> {
    const params = new HttpParams().set('includeInactive', includeInactive.toString());
    return this.http.get<AnalyticsSummaryResponse>(`${this.baseUrl}/summary`, { params });
  }

  getSalaryByCountry(includeInactive: boolean = false): Observable<BreakdownItemResponse[]> {
    const params = new HttpParams().set('includeInactive', includeInactive.toString());
    return this.http.get<BreakdownItemResponse[]>(`${this.baseUrl}/by-country`, { params });
  }

  getSalaryByDepartment(includeInactive: boolean = false): Observable<BreakdownItemResponse[]> {
    const params = new HttpParams().set('includeInactive', includeInactive.toString());
    return this.http.get<BreakdownItemResponse[]>(`${this.baseUrl}/by-department`, { params });
  }

  getSalaryByJobLevel(includeInactive: boolean = false): Observable<BreakdownItemResponse[]> {
    const params = new HttpParams().set('includeInactive', includeInactive.toString());
    return this.http.get<BreakdownItemResponse[]>(`${this.baseUrl}/by-job-level`, { params });
  }

  getSalaryDistribution(includeInactive: boolean = false): Observable<SalaryDistributionItemResponse[]> {
    const params = new HttpParams().set('includeInactive', includeInactive.toString());
    return this.http.get<SalaryDistributionItemResponse[]>(`${this.baseUrl}/salary-distribution`, { params });
  }
}