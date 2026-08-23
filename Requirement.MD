# Requirements Document – v1
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

**Data Model (Core Fields):**
- Employee ID
- Full Name
- Email
- Department
- Job Title
- Job Level
- Country
- Currency
- Annual Salary
- Status (Active/Inactive)
- Date of Joining
- Last Updated (system managed)

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

## 4. Out of Scope (Deliberately Excluded)

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

---

## 5. Non-Functional Expectations
- Support 10,000 employee records comfortably.
- Clean, responsive UI suitable for daily HR use.
- Fast filtering, searching, and pagination.
- Production-quality code with meaningful unit tests for core logic.

---

**Document Status:** v1  
**Next Step:** Review and refine Analytics depth + final data model decisions before implementation.