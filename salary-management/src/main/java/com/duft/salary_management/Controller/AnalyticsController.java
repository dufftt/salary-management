package com.duft.salary_management.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duft.salary_management.DTO.AnalyticsSummaryResponse;
import com.duft.salary_management.DTO.BreakdownItemResponse;
import com.duft.salary_management.DTO.SalaryDistributionItemResponse;
import com.duft.salary_management.Service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> getSummary(
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(analyticsService.getSummary(includeInactive));
    }

    @GetMapping("/by-country")
    public ResponseEntity<List<BreakdownItemResponse>> getByCountry(
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(analyticsService.getSalaryByCountry(includeInactive));
    }

    @GetMapping("/by-department")
    public ResponseEntity<List<BreakdownItemResponse>> getByDepartment(
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(analyticsService.getSalaryByDepartment(includeInactive));
    }

    @GetMapping("/by-job-level")
    public ResponseEntity<List<BreakdownItemResponse>> getByJobLevel(
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(analyticsService.getSalaryByJobLevel(includeInactive));
    }

    @GetMapping("/salary-distribution")
    public ResponseEntity<List<SalaryDistributionItemResponse>> getSalaryDistribution(
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(analyticsService.getSalaryDistribution(includeInactive));
    }
}
