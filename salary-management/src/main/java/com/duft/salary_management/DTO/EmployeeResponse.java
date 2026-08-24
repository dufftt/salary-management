package com.duft.salary_management.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Currency;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;

public record EmployeeResponse(
        UUID id,
        String employeeId,
        String fullName,
        String email,
        Department department,
        String jobTitle,
        JobLevel jobLevel,
        Country country,
        Currency currency,
        BigDecimal annualSalary,
        BigDecimal annualSalaryUsd,
        EmployeeStatus status,
        LocalDate dateOfJoining,
        Instant createdAt,
        Instant updatedAt
) {}