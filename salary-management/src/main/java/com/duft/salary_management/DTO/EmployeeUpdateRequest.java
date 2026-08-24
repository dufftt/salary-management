package com.duft.salary_management.DTO;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Currency;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;

public record EmployeeUpdateRequest(

        @NotBlank
        @Size(min = 2, max = 150)
        String fullName,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotNull
        Department department,

        @NotBlank
        @Size(min = 2, max = 150)
        String jobTitle,

        @NotNull
        JobLevel jobLevel,

        @NotNull
        Country country,

        @NotNull
        Currency currency,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal annualSalary,

        @NotNull
        LocalDate dateOfJoining,

        @NotNull
        EmployeeStatus status

) {}
