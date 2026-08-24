# Architecture Document
**ACME Employee Salary Management System**

## 1. Architecture Style

We are using a **Clean Layered Architecture**.

```text
┌─────────────────────────────────────────────┐
│               Angular Frontend              │
│     (Employees + Analytics Features)        │
└────────────────────┬────────────────────────┘
                     │ REST API (JSON)
┌────────────────────▼────────────────────────┐
│              Spring Boot Backend            │
│                                             │
│  ┌──────────────┐      ┌─────────────────┐  │
│  │  Controller  │ ───► │     Service     │  │
│  │    Layer     │      │     Layer       │  │
│  └──────────────┘      └────────┬────────┘  │
│                                 │           │
│                        ┌────────▼────────┐  │
│                        │   Repository    │  │
│                        │     Layer       │  │
│                        └────────┬────────┘  │
│                                 │           │
│                        ┌────────▼────────┐  │
│                        │  Database (JPA) │  │
│                        └─────────────────┘  │
└─────────────────────────────────────────────┘
```


## API Design (High Level)
## Employee Management

|GET  |  /api/employees                 |     Paginated + filtered + sorted list|
|-----|---------------------------------|---------------------------------------|     
|GET  |  /api/employees/{id}            |   → Get single employee               |
|POST |  /api/employees                 |   → Create employee                   |
|PUT  |  /api/employees/{id}            |   → Update employee                   |
|PATCH|  /api/employees/{id}/deactivate |   → Soft delete                       |

## Analytics

|GET | /api/analytics/summary
|----|----------------------------
|GET | /api/analytics/by-country
|GET | /api/analytics/by-department
|GET | /api/analytics/by-job-level
|GET | /api/analytics/salary-distribution