import { Routes } from '@angular/router';
import { EmployeeListComponent } from './features/employee/employee-list/employee-list.component';
import { EmployeeFormComponent } from './features/employee/employee-form/employee-form.component';
import { EmployeeDetailComponent } from './features/employee/employee-details/employee-detail.component';
import { AnalyticsDashboardComponent } from './features/analytics-dashboard/analytics-dashboard.component';

export const routes: Routes = [

  { path: '', redirectTo: 'employees', pathMatch: 'full' },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'employees/new', component: EmployeeFormComponent },
  { path: 'employees/:id', component: EmployeeDetailComponent },
  { path: 'employees/:id/edit', component: EmployeeFormComponent },
  { path: 'analytics', component: AnalyticsDashboardComponent },
  { path: '**', redirectTo: 'employees' }

];
