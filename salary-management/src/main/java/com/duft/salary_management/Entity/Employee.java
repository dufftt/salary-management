package com.duft.salary_management.Entity;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Currency;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_id", columnList = "employee_id", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_country", columnList = "country"),
        @Index(name = "idx_department", columnList = "department"),
        @Index(name = "idx_job_level", columnList = "job_level"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_annual_salary", columnList = "annual_salary"),
        @Index(name = "idx_annual_salary_usd", columnList = "annual_salary_usd")
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "TEXT")
    private UUID id;

    @Column(name = "employee_id", nullable = false, unique = true, length = 50)
    private String employeeId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Department department;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", nullable = false, length = 50)
    private JobLevel jobLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(name = "annual_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal annualSalary;

    @Column(name = "annual_salary_usd", nullable = false, precision = 12, scale = 2)
    private BigDecimal annualSalaryUsd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status;

    @Column(name = "date_of_joining", nullable = false)
    private LocalDate dateOfJoining;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // --- Constructors ---

    public Employee() {
    }

    public Employee(String employeeId, String fullName, String email, Department department,
                    String jobTitle, JobLevel jobLevel, Country country, Currency currency,
                    BigDecimal annualSalary, BigDecimal annualSalaryUsd, EmployeeStatus status,
                    LocalDate dateOfJoining) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.jobTitle = jobTitle;
        this.jobLevel = jobLevel;
        this.country = country;
        this.currency = currency;
        this.annualSalary = annualSalary;
        this.annualSalaryUsd = annualSalaryUsd;
        this.status = status;
        this.dateOfJoining = dateOfJoining;
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public JobLevel getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(JobLevel jobLevel) {
        this.jobLevel = jobLevel;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal getAnnualSalaryUsd() {
        return annualSalaryUsd;
    }

    public void setAnnualSalaryUsd(BigDecimal annualSalaryUsd) {
        this.annualSalaryUsd = annualSalaryUsd;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}