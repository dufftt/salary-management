# DTO Design Document
**ACME Employee Salary Management System**

## 1. Mapping Strategy

We will use **MapStruct** for all entity ↔ DTO mappings.

**Reasons:**
- Compile-time safety
- Excellent performance
- Clean and maintainable code
- Industry standard in modern Spring Boot projects

We will **not** use ModelMapper or manual mapping (except in very simple cases).

---

## 2. General Principles

- DTOs are separate from JPA entities (entities are never exposed).
- Prefer Java `record` for DTOs (immutable).
- Validation annotations (`@NotNull`, `@Email`, `@Positive`, etc.) will be placed on Request DTOs.
- Enums will be used for controlled values (`Department`, `JobLevel`, `Country`, `Currency`, `EmployeeStatus`).

---

## 3. Request DTOs

### EmployeeCreateRequest

Used for `POST /api/employees`

| Field            | Type              | Validation                          | Notes |
|------------------|-------------------|-------------------------------------|-------|
| employeeId       | String            | @NotBlank, unique format            | Required |
| fullName         | String            | @NotBlank, size 2–150               | |
| email            | String            | @NotBlank, @Email, unique           | |
| department       | Department (Enum) | @NotNull                            | |
| jobTitle         | String            | @NotBlank, size 2–150               | |
| jobLevel         | JobLevel (Enum)   | @NotNull                            | |
| country          | Country (Enum)    | @NotNull                            | |
| currency         | Currency (Enum)   | @NotNull                            | |
| annualSalary     | BigDecimal        | @NotNull, @Positive                 | |
| dateOfJoining    | LocalDate         | @NotNull                            | |

- `status` is **not** accepted in the request. It will always be set to `ACTIVE` on creation.

---

### EmployeeUpdateRequest

Used for `PUT /api/employees/{id}`

| Field            | Type              | Validation                          | Notes |
|------------------|-------------------|-------------------------------------|-------|
| fullName         | String            | @NotBlank, size 2–150               | |
| email            | String            | @NotBlank, @Email                   | |
| department       | Department (Enum) | @NotNull                            | |
| jobTitle         | String            | @NotBlank, size 2–150               | |
| jobLevel         | JobLevel (Enum)   | @NotNull                            | |
| country          | Country (Enum)    | @NotNull                            | |
| currency         | Currency (Enum)   | @NotNull                            | |
| annualSalary     | BigDecimal        | @NotNull, @Positive                 | |
| dateOfJoining    | LocalDate         | @NotNull                            | |
| status           | EmployeeStatus    | @NotNull                            | |

- `employeeId` and `id` are **not** updatable.
- We are using **full replacement** semantics (PUT).

---

## 4. Response DTOs

### EmployeeResponse

Returned for single employee and inside lists.

| Field            | Type              | Notes |
|------------------|-------------------|-------|
| id               | UUID / Long       | |
| employeeId       | String            | |
| fullName         | String            | |
| email            | String            | |
| department       | String / Enum     | |
| jobTitle         | String            | |
| jobLevel         | String / Enum     | |
| country          | String / Enum     | |
| currency         | String / Enum     | |
| annualSalary     | BigDecimal        | |
| status           | String / Enum     | |
| dateOfJoining    | LocalDate         | |
| createdAt        | Instant           | |
| updatedAt        | Instant           | |

---

### PageResponse\<T\>

Generic pagination wrapper.

| Field            | Type              | Notes |
|------------------|-------------------|-------|
| content          | List\<T\>         | |
| page             | int               | Current page (0-based or 1-based — we will decide) |
| size             | int               | |
| totalElements    | long              | |
| totalPages       | int               | |
| first            | boolean           | |
| last             | boolean           | |

---

## 5. Analytics DTOs

### AnalyticsSummaryResponse

| Field                 | Type         |
|-----------------------|--------------|
| totalHeadcount        | long         |
| totalAnnualPayroll    | BigDecimal   |
| averageAnnualSalary   | BigDecimal   |
| medianAnnualSalary    | BigDecimal   |
| minSalary             | BigDecimal   |
| maxSalary             | BigDecimal   |

---

### BreakdownItemResponse

Used for "by Country", "by Department", "by Job Level"

| Field            | Type         | Notes |
|------------------|--------------|-------|
| key              | String       | e.g. "India", "Engineering", "L3" |
| averageSalary    | BigDecimal   | |
| totalSalary      | BigDecimal   | |
| headcount        | long         | |

---

### SalaryDistributionItemResponse

| Field            | Type         | Notes |
|------------------|--------------|-------|
| band             | String       | e.g. "0 - 50,000", "50,001 - 100,000" |
| count            | long         | |
| percentage       | double       | |

---

## 6. Mapping Rules (MapStruct)

| Source                     | Target                        | Notes |
|---------------------------|-------------------------------|-------|
| EmployeeCreateRequest     | Employee                      | Set `status = ACTIVE`, handle timestamps |
| EmployeeUpdateRequest     | Employee                      | Full update |
| Employee                  | EmployeeResponse              | Standard mapping |
| Page\<Employee\>          | PageResponse\<EmployeeResponse\> | Map content + pagination fields |

---

## 7. Key Decisions Summary

| Decision                  | Choice                          |
|---------------------------|---------------------------------|
| Mapping library           | MapStruct                       |
| DTO style                 | Java `record` (preferred)       |
| Create / Update DTOs      | Separate                        |
| Update semantics          | Full replacement (PUT)          |
| Status on Create          | Defaulted to `ACTIVE`           |
| Entity exposure           | Never                           |
| Pagination wrapper        | Custom `PageResponse<T>`        |

---

**Document Status:** v1  
**Last Updated:** Initial DTO design