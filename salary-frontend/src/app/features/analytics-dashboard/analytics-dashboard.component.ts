import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData } from 'chart.js';

import { AnalyticsService } from '../../core/services/analytics.service';
import {
  AnalyticsSummaryResponse,
  BreakdownItemResponse,
  SalaryDistributionItemResponse
} from '../../shared/components/models/analytics.model';

@Component({
  selector: 'app-analytics-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatCheckboxModule,
    MatButtonModule,
    BaseChartDirective
  ],
  templateUrl: './analytics-dashboard.component.html',
  styleUrl: './analytics-dashboard.component.scss'
})
export class AnalyticsDashboardComponent implements OnInit {

  includeInactive = false;

  summary: AnalyticsSummaryResponse | null = null;

  // Chart data
  countryChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  departmentChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  jobLevelChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  distributionChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };

  // Common chart options
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => '$' + Number(value).toLocaleString()
        }
      }
    }
  };

  doughnutOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right'
      }
    }
  };

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  onToggleInactive(): void {
    this.loadAllData();
  }

  private loadAllData(): void {
    this.loadSummary();
    this.loadByCountry();
    this.loadByDepartment();
    this.loadByJobLevel();
    this.loadDistribution();
  }

  private loadSummary(): void {
    this.analyticsService.getSummary(this.includeInactive).subscribe({
      next: (data) => this.summary = data
    });
  }

  private loadByCountry(): void {
    this.analyticsService.getSalaryByCountry(this.includeInactive).subscribe({
      next: (data) => {
        this.countryChartData = this.toBarChartData(data, 'Average Salary by Country');
      }
    });
  }

  private loadByDepartment(): void {
    this.analyticsService.getSalaryByDepartment(this.includeInactive).subscribe({
      next: (data) => {
        this.departmentChartData = this.toBarChartData(data, 'Average Salary by Department');
      }
    });
  }

  private loadByJobLevel(): void {
    this.analyticsService.getSalaryByJobLevel(this.includeInactive).subscribe({
      next: (data) => {
        this.jobLevelChartData = this.toBarChartData(data, 'Average Salary by Job Level');
      }
    });
  }

  private loadDistribution(): void {
    this.analyticsService.getSalaryDistribution(this.includeInactive).subscribe({
      next: (data: SalaryDistributionItemResponse[]) => {
        this.distributionChartData = {
          labels: data.map(d => d.band),
          datasets: [
            {
              data: data.map(d => d.count),
              backgroundColor: [
                '#42A5F5',
                '#66BB6A',
                '#FFA726',
                '#AB47BC',
                '#EC407A',
                '#26C6DA',
                '#FF7043'
              ]
            }
          ]
        };
      }
    });
  }

  private toBarChartData(data: BreakdownItemResponse[], label: string): ChartData<'bar'> {
    return {
      labels: data.map(d => d.key),
      datasets: [
        {
          data: data.map(d => d.averageSalary),
          label: label,
          backgroundColor: '#3f51b5',
          borderRadius: 4
        }
      ]
    };
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(value);
  }
}