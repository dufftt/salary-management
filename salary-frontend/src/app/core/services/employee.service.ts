import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../enviroment/environment';
import {
  EmployeeCreateRequest,
  EmployeeResponse,
  EmployeeSearchParams,
  EmployeeUpdateRequest,
  PageResponse
} from '../../shared/components/models/employee.model';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly baseUrl = `${environment.apiUrl}/employees`;

  constructor(private http: HttpClient) {}

  /** Search + Filter + Pagination */
  searchEmployees(params: EmployeeSearchParams = {}): Observable<PageResponse<EmployeeResponse>> {
    let httpParams = new HttpParams();

    if (params.search) {
      httpParams = httpParams.set('search', params.search);
    }
    if (params.country) {
      httpParams = httpParams.set('country', params.country);
    }
    if (params.department) {
      httpParams = httpParams.set('department', params.department);
    }
    if (params.jobLevel) {
      httpParams = httpParams.set('jobLevel', params.jobLevel);
    }
    if (params.status) {
      httpParams = httpParams.set('status', params.status);
    }
    if (params.page !== undefined) {
      httpParams = httpParams.set('page', params.page.toString());
    }
    if (params.size !== undefined) {
      httpParams = httpParams.set('size', params.size.toString());
    }
    if (params.sort) {
      httpParams = httpParams.set('sort', params.sort);
    }

    return this.http.get<PageResponse<EmployeeResponse>>(this.baseUrl, { params: httpParams });
  }

  /** Get single employee */
  getEmployeeById(id: string): Observable<EmployeeResponse> {
    return this.http.get<EmployeeResponse>(`${this.baseUrl}/${id}`);
  }

  /** Create employee */
  createEmployee(request: EmployeeCreateRequest): Observable<EmployeeResponse> {
    return this.http.post<EmployeeResponse>(this.baseUrl, request);
  }

  /** Update employee */
  updateEmployee(id: string, request: EmployeeUpdateRequest): Observable<EmployeeResponse> {
    return this.http.put<EmployeeResponse>(`${this.baseUrl}/${id}`, request);
  }

  /** Soft delete (Deactivate) */
  deactivateEmployee(id: string): Observable<EmployeeResponse> {
    return this.http.patch<EmployeeResponse>(`${this.baseUrl}/${id}/deactivate`, {});
  }
}