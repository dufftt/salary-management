# Requirements Document – v3
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
Provide a curated set of insights so the HR Manager can understand compensation patterns:

**Summary Metrics (KPI Cards):**
- Total Headcount
- Total Annual Payroll Cost
- Average Annual Salary
- Median Annual Salary
- Salary Range (Min – Max)

**Key Breakdowns:**
- Average Annual Salary by Country
- Average Annual Salary by Department
- Average Annual Salary by Job Level
- Salary Distribution (using salary bands)

All metrics and charts will respect the applied filters (Country, Department, Job Level, Status).  
Support CSV export of the current filtered/aggregated view.

---

## 4. Data Model

We are intentionally using a **single `employees` table** with controlled string values instead of full normalization.

**Reason:**  
Full normalization would add unnecessary complexity for the current scope (CRUD + basic analytics). A single table with strictly controlled values keeps the system simple, queries fast, and development focused, while still maintaining good data consistency through application-level validation.

### Table: `employees`

| Column              | Type                  | Constraints / Notes                                      | Required |
|---------------------|-----------------------|----------------------------------------------------------|----------|
| `id`                | UUID / BigSerial      | Primary Key                                              | Yes     |
| `employee_id`       | VARCHAR(50)           | Unique business identifier (e.g. ACME-10001)             | Yes     |
| `full_name`         | VARCHAR(150)          |                                                          | Yes     |
| `email`             | VARCHAR(150)          | Unique                                                   | Yes     |
| `department`        | VARCHAR(100)          | Controlled list                                          | Yes     |
| `job_title`         | VARCHAR(150)          |                                                          | Yes     |
| `job_level`         | VARCHAR(50)           | Controlled list                                          | Yes     |
| `country`           | VARCHAR(100)          | Controlled list                                          | Yes     |
| `currency`          | CHAR(3)               | ISO currency code                                        | Yes     |
| `annual_salary`     | DECIMAL(12,2)         | Must be > 0                                              | Yes     |
| `status`            | VARCHAR(20)           | `Active` or `Inactive`                                   | Yes     |
| `date_of_joining`   | DATE                  |                                                          | Yes     |
| `created_at`        | TIMESTAMP             | Auto-set on create                                       | Yes     |
| `updated_at`        | TIMESTAMP             | Auto-updated on change                                   | Yes     |

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
- Unique index on `employee_id`
- Unique index on `email`
- Index on `country`, `department`, `job_level`, `status`, `annual_salary`

---

## 5. Validation Rules

All validation will be enforced on the backend. Frontend will mirror the same rules for better user experience.

| Field              | Validation Rules                                                                 |
|--------------------|----------------------------------------------------------------------------------|
| `employee_id`      | Required, Unique, 3–50 characters, alphanumeric + hyphen only                    |
| `full_name`        | Required, 2–150 characters, trimmed                                              |
| `email`            | Required, Valid email format, Unique, Max 150 characters                         |
| `department`       | Required, Must be one of the controlled values                                   |
| `job_title`        | Required, 2–150 characters                                                       |
| `job_level`        | Required, Must be one of the controlled values                                   |
| `country`          | Required, Must be one of the controlled values                                   |
| `currency`         | Required, Must be one of the controlled values                                   |
| `annual_salary`    | Required, Greater than 0, Maximum 50,000,000                                     |
| `status`           | Required, Only `Active` or `Inactive`                                            |
| `date_of_joining`  | Required, Valid date, Cannot be more than 1 year in the future                   |

**Additional Rules:**
- All string inputs must be trimmed.
- `employee_id` should not be editable after creation.
- Controlled values are case-sensitive and must match exactly.

---

## 6. Migration & Seeding Strategy

### Schema Migration
- All database schema changes will be managed through versioned migration scripts.
- Tool: **Flyway** (recommended with Spring Boot)
- No manual changes to the database schema are allowed.
- Initial migration will create the `employees` table along with all indexes and constraints.

### Data Seeding
- A dedicated seed script/command will generate **10,000 realistic employee records**.
- Seeding is separate from schema migrations.
- Seed data will:
  - Respect all controlled values
  - Create realistic distribution across countries, departments, and job levels
  - Make salary ranges correlate reasonably with Job Level and Country
  - Ensure good variety so that analytics charts are meaningful

---

## 7. Tech Stack

| Layer          | Technology                          | Notes |
|----------------|-------------------------------------|-------|
| Backend        | Java + Spring Boot                  | REST API |
| ORM            | Spring Data JPA                     | |
| Validation     | Jakarta Bean Validation             | |
| Database       | PostgreSQL (primary) / SQLite       | SQLite acceptable for simplicity |
| Migration      | Flyway                              | |
| Frontend       | Angular                             | |
| UI Library     | Angular Material                    | |
| Charts         | Chart.js or ng2-charts              | For analytics |

---

## 8. Out of Scope (Deliberately Excluded)

| Item                                      | Reason for Exclusion                                      |
|-------------------------------------------|-----------------------------------------------------------|
| Employee self-service portal              | Primary persona is HR Manager only                        |
| Bulk Excel/CSV upload                     | Adds complexity; can be added later if needed             |
| Salary revision history / versioning      | Not required for core management + insights               |
| Approval workflows                        | Out of scope for this assessment                          |
| Multi-currency conversion engine          | Store salary in original currency; no live conversion     |
| Advanced self-service BI / custom chart builder | High complexity with limited incremental value     |
| Role-based access control (multiple HR roles) | Single primary persona                                |
| Historical trend analysis (YoY)           | No historical data model in current scope                 |
| Pay equity / diversity analysis           | Sensitive and beyond current requirements                 |
| Fully normalized lookup tables            | Intentionally avoided to prevent over-engineering         |

---

## 9. Non-Functional Expectations
- Support 10,000 employee records comfortably.
- Clean, responsive UI suitable for daily HR use.
- Fast filtering, searching, and pagination.
- Production-quality code with meaningful unit tests for core logic.
- Incremental commits that show the evolution of the solution.

---

**Document Status:** v3  
**Last Updated:** Validation rules, migration strategy, and Tech Stack (Java + Angular) added