package com.duft.salary_management.DTO;

public record SalaryDistributionItemResponse(
        String band,
        long count,
        double percentage
) {}
