# Requirements Document – v5
**ACME Employee Salary Management System**

## 1. Goal
Build a focused web-based application that allows the HR Manager to efficiently manage salary data for 10,000 employees across multiple countries and gain clear insights into how the organization pays its people.

## 2. User Persona
**Primary User:** HR Manager  
Responsible for maintaining accurate employee salary records and answering compensation-related questions for the organization.

---

## 3. Scope & Features

### A. Employee Salary Management
- View a paginated list of all employees with key information (Name, Employee ID, Department, Country, Job Level, Annual Salary + Currency, Status).
- Search employees by Name, Employee ID, or Email.
- Filter employees by:
  - Country
  - Department
  - Job Level
  - Status (Active / Inactive)
- Sort by Name, Annual Salary, Country, Department, or Date of Joining.
- View detailed information of an individual employee.
- Add a new employee salary record.
- Edit an existing employee salary record.
- Soft-delete (Deactivate) an employee.

### B. Analytics & Insights
Provide a curated set of insights so the HR Manager can understand compensation patterns.

**Summary Metrics (KPI Cards):**
- Total Headcount
- Total Annual Payroll Cost (in USD)
- Average Annual Salary (in USD)
- Median Annual Salary (in USD)
- Salary Range (Min – Max in USD)

**Key Breakdowns:**
- Average Annual Salary by Country
- Average Annual Salary by Department
- Average Annual Salary by Job Level
- Salary Distribution (using salary bands in USD)

**Rules:**
- By default, analytics only include **Active** employees.
- A checkbox “Include Inactive employees” will be available (unchecked by default).
- All aggregations are performed in the database.
- All analytics calculations use the normalized `annual_salary_usd` field.

---

## 4. Data Model

We are using a **single `employees` table** with controlled string/enum values.

### Table: `employees`

| Column                | Type                  | Constraints / Notes                                      | Required |
|-----------------------|-----------------------|----------------------------------------------------------|----------|
| `id`                  | UUID / TEXT           | Primary Key                                              | Yes     |
| `employee_id`         | VARCHAR(50)           | Unique business identifier                               | Yes     |
| `full_name`           | VARCHAR(150)          |                                                          | Yes     |
| `email`               | VARCHAR(150)          | Unique                                                   | Yes     |
| `department`          | VARCHAR(100)          | Controlled list / Enum                                   | Yes     |
| `job_title`           | VARCHAR(150)          |                                                          | Yes     |
| `job_level`           | VARCHAR(50)           | Controlled list / Enum                                   | Yes     |
| `country`             | VARCHAR(100)          | Controlled list / Enum                                   | Yes     |
| `currency`            | CHAR(3)               | Original currency                                        | Yes     |
| `annual_salary`       | NUMERIC(12,2)         | Original salary                                          | Yes     |
| `annual_salary_usd`   | NUMERIC(12,2)         | Normalized salary for analytics                          | Yes     |
| `status`              | VARCHAR(20)           | `Active` or `Inactive`                                   | Yes     |
| `date_of_joining`     | DATE                  |                                                          | Yes     |
| `created_at`          | TIMESTAMP             | Auto-set on create                                       | Yes     |
| `updated_at`          | TIMESTAMP             | Auto-updated on change                                   | Yes     |

### Multi-Currency Handling (Approach B)

- We store both the **original salary** (`annual_salary` + `currency`) and a normalized value (`annual_salary_usd`).
- Conversion to USD happens **only on Create and Update**.
- List page and Detail page always show the **original salary + currency**.
- All analytics use `annual_salary_usd`.
- Conversion rates are maintained as a static map for the prototype.

### Controlled Values

**Departments:**  
Engineering, Product, Design, Marketing, Sales, Human Resources, Finance, Operations, Customer Success, Legal  

**Job Levels:**  
L1, L2, L3, L4, L5, Manager, Senior Manager, Director, VP  

**Countries:**  
India, United States, United Kingdom, Germany, Singapore, Australia, Canada, Netherlands, UAE, Japan  

**Currencies:**  
INR, USD, EUR, GBP, SGD, AUD, CAD, AED, JPY

### Indexes
- Unique: `employee_id`, `email`
- Index: `country`, `department`, `job_level`, `status`, `annual_salary`, `annual_salary_usd`

---

## 5. Validation Rules

| Field              | Validation Rules                                                                 |
|--------------------|----------------------------------------------------------------------------------|
| `employee_id`      | Required, Unique, 3–50 characters, alphanumeric + hyphen                         |
| `full_name`        | Required, 2–150 characters                                                       |
| `email`            | Required, Valid email, Unique                                                    |
| `department`       | Required, Must be one of the controlled values                                   |
| `job_title`        | Required, 2–150 characters                                                       |
| `job_level`        | Required, Must be one of the controlled values                                   |
| `country`          | Required, Must be one of the controlled values                                   |
| `currency`         | Required, Must be one of the controlled values                                   |
| `annual_salary`    | Required, > 0, Maximum 50,000,000                                                |
| `status`           | Required, Only `Active` or `Inactive`                                            |
| `date_of_joining`  | Required, Cannot be more than 1 year in the future                               |

- `annual_salary_usd` is **calculated** on the server (not accepted from client).
- `employee_id` is not editable after creation.

---

## 6. Migration & Seeding Strategy

- Schema changes managed via **Flyway**.
- Dedicated seed script will create **10,000 realistic employees**.
- Seed data will:
  - Respect controlled values
  - Create realistic distribution across countries, departments, and job levels
  - Calculate `annual_salary_usd` during seeding
  - Make salary somewhat correlated with Job Level and Country

---

## 7. Tech Stack

| Layer          | Technology                          |
|----------------|-------------------------------------|
| Backend        | Java + Spring Boot                  |
| ORM            | Spring Data JPA                     |
| Validation     | Jakarta Bean Validation             |
| Database       | SQLite                              |
| Migration      | Flyway                              |
| Mapping        | MapStruct                           |
| Frontend       | Angular + Angular Material          |
| Charts         | Chart.js / ng2-charts               |

---

## 8. Architecture

We are using **Clean Layered Architecture**:

- Controller → Service → Repository
- Entities are never exposed (DTOs are used)
- JPA Specifications for dynamic filtering
- Some manual JPQL where it provides better clarity or performance

Detailed architecture is documented separately in `architecture.md`.

---

## 9. Key Design Decisions & Trade-offs

### 9.1 Caching Decision

**Decision:** Caching is **not** implemented in the current version.

**Reasoning:**
- The system only manages 10,000 employee records.
- With proper database indexes and aggregations pushed to the database, query performance is already excellent.
- Adding a caching layer (along with proper cache invalidation on Create/Update/Deactivate) would introduce additional complexity without meaningful performance benefits at this scale.
- This decision follows the principle of avoiding over-engineering.

**Future Consideration:**  
A targeted cache (e.g. Caffeine) can be added later specifically for analytics endpoints if usage patterns or data volume justify it.

### 9.2 Other Important Decisions
- Single table design instead of full normalization → reduces complexity while maintaining data consistency through enums + validation.
- Clean Layered Architecture instead of Hexagonal → appropriate for the current domain complexity.
- Median calculation done in application memory (for SQLite compatibility) → acceptable for 10k records.
- Analytics default to Active employees only.

---

## 10. Out of Scope

| Item                                      | Reason |
|-------------------------------------------|--------|
| Caching layer                             | Unnecessary complexity at current scale (see section 9.1) |
| Employee self-service portal              | Only HR Manager persona |
| Bulk Excel/CSV upload                     | Complexity |
| Salary revision history                   | Not required |
| Approval workflows                        | Out of scope |
| Live exchange rate API                    | Static rates are sufficient |
| Fully flexible self-service BI            | Over-engineering |
| Role-based access control                 | Single persona |
| Fully normalized lookup tables            | Intentionally avoided |
| Hexagonal / Ports & Adapters              | Unnecessary complexity for this scope |

---

## 11. Non-Functional Expectations
- Comfortably support 10,000 employee records
- Fast filtering, searching, and pagination
- All heavy aggregations performed in the database
- Production-quality code with meaningful unit tests
- Incremental commits that show evolution of the solution
- Avoid over-engineering (caching, complex architectural patterns, etc.)

---

**Document Status:** v5  
**Key Changes in v5:**
- Explicitly documented the decision of **not implementing caching** along with clear reasoning.
- Added dedicated "Key Design Decisions & Trade-offs" section.