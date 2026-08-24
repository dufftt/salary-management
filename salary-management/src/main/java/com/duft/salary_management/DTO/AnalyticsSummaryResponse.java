package com.duft.salary_management.DTO;

import java.math.BigDecimal;

public record AnalyticsSummaryResponse(
        long totalHeadcount,
        BigDecimal totalAnnualPayroll,
        BigDecimal averageAnnualSalary,
        BigDecimal medianAnnualSalary,
        BigDecimal minSalary,
        BigDecimal maxSalary
) {}
