# Requirements Document – v2
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
Full normalization (separate tables for Department, Job Level, Country, etc.) would add unnecessary complexity for the current scope (CRUD + basic analytics). A single table with strictly controlled values keeps the system simple, queries fast, and development focused, while still maintaining good data consistency through application-level validation.

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

## 5. Out of Scope (Deliberately Excluded)

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

## 6. Non-Functional Expectations
- Support 10,000 employee records comfortably.
- Clean, responsive UI suitable for daily HR use.
- Fast filtering, searching, and pagination.
- Production-quality code with meaningful unit tests for core logic.

---

**Document Status:** v2  
**Last Updated:** Data model finalized (single table approach)