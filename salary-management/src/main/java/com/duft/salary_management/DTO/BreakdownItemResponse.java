package com.duft.salary_management.DTO;


import java.math.BigDecimal;

public record BreakdownItemResponse(
        String key,
        BigDecimal averageSalary,
        BigDecimal totalSalary,
        long headcount
) {}
