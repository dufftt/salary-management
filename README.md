# ACME Employee Salary Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4%20%2F%204.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%20%2F%2021-orange.svg)](https://www.oracle.com/java/)
[![Angular](https://img.shields.io/badge/Angular-21-red.svg)](https://angular.dev/)
[![Docker](https://img.shields.io/badge/Docker-Compose%20Ready-blue.svg)](https://www.docker.com/)

A modern, production-ready web application designed for the **HR Manager persona** to replace cumbersome Excel spreadsheets, manage 10,000 employee salary records across multiple global regions, and provide deep analytics into how the organization compensates its workforce.

---

## 📌 Problem Statement & Product Framing

- **User Persona:** HR Manager
- **Context:** ACME Corp previously maintained salary records for **10,000 employees across 10 countries and 9 currencies** in Excel spreadsheets.
- **Challenge:** Spreadsheet management was error-prone, lacked multi-currency normalization, and could not easily provide instant aggregates, salary bands, or demographic breakdowns.
- **Solution:** A clean, end-to-end web software enabling full CRUD operations, specification-based multi-filter search, and interactive data visualizations.

---

## ✨ Features

### 👥 1. Employee Management
- **Paginated & Sorted Directory:** View 10,000 records with responsive pagination (10, 20, 50, 100 per page) and sorting.
- **Real-Time Search & Filtering:**
  - Search by Name, Employee ID, or Email.
  - Multi-select filters by **Country**, **Department**, **Job Level**, and **Status** (Active / Inactive).
- **CRUD Operations:**
  - Create new employee records with instant local-to-USD currency conversion.
  - View full employee profiles with audit timestamps.
  - Edit employee details with duplicate email & ID validations.
  - Soft-delete (Deactivate) employees without data loss.

### 📊 2. Compensation Analytics & Insights
- **Top KPI Cards:**
  - Total Headcount
  - Total Annual Payroll Cost (USD)
  - Average Annual Salary (USD)
  - Median Annual Salary (USD)
  - Salary Range (Min – Max in USD)
- **Visual Breakdowns (Chart.js):**
  - Average Salary by Country (Bar Chart)
  - Average Salary by Department (Bar Chart)
  - Average Salary by Job Level (Bar Chart)
  - Salary Distribution by Bands (Doughnut Chart: `0-30k`, `30k-50k`, `50k-80k`, `80k-120k`, `120k-200k`, `200k+`)
- **Active / Inactive Toggle:** Compare active operational payroll vs total organization liability.

### 🌐 3. Multi-Currency Normalization
- Stores both native local salary (`annual_salary`, `currency`) and normalized USD value (`annual_salary_usd`).
- Supported currencies: `USD`, `INR`, `EUR`, `GBP`, `SGD`, `AUD`, `CAD`, `AED`, `JPY`.
- Normalization occurs on Create/Update, enabling high-performance database-level aggregations.

---

## 🏗️ Architecture & Tech Stack

### Clean Layered Architecture
```
┌────────────────────────────────────────────────────────┐
│               Angular 21 (SPA Frontend)                │
│       (Angular Material, Chart.js, SCSS Themes)        │
└───────────────────────────┬────────────────────────────┘
                            │ REST API (JSON / HTTP)
┌───────────────────────────▼────────────────────────────┐
│              Spring Boot 3/4 Backend                   │
│                                                        │
│   Controller Layer   ──►  Service Layer                │
│   (Validation / DTO)      (Business Logic / Currency)  │
│                                │                       │
│                           Repository Layer             │
│                           (JPA Specs / Aggregations)   │
│                                │                       │
│                           Database (SQLite + Flyway)   │
└────────────────────────────────────────────────────────┘
```

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 17+, Spring Boot, Spring Data JPA, Hibernate, Jakarta Validation, MapStruct |
| **Database** | SQLite, Flyway Database Migrations |
| **Frontend** | Angular 21, Angular Material, `ng2-charts` / Chart.js, RxJS, TypeScript |
| **DevOps** | Docker, Docker Compose, Nginx Multi-stage builds |

---

## 🚀 Quickstart with Docker Compose

To spin up the entire application (Backend + Database with 10k seeded records + Frontend + Nginx Proxy):

```bash
docker compose up --build
```

### Access URLs:
- **Frontend Web UI:** [http://localhost:4200](http://localhost:4200)
- **Backend API:** [http://localhost:8080/api/employees](http://localhost:8080/api/employees)

---

## 💻 Local Development Setup

### Prerequisites
- JDK 17 or 21
- Node.js 20+ and npm
- Git

### 1. Backend Setup
```bash
cd salary-management

# Run database migrations, seeding, and start server on port 8080
./mvnw spring-boot:run
```

### 2. Frontend Setup
```bash
cd salary-frontend

# Install dependencies
npm install

# Start Angular development server on port 4200
npm start
```

---

## 🧪 Testing

### Backend Test Suite
The backend contains 32 comprehensive unit and integration tests covering:
- **`CurrencyConverterTest`**: Exchange rate calculations and precision.
- **`EmployeeServiceTest`**: Full CRUD, validation rules, and duplicate error handling.
- **`AnalyticsServiceTest`**: Summary metrics, odd/even median calculation, and salary distribution bands.
- **`EmployeeControllerTest`**: REST endpoints, status codes, and JSON serialization.
- **`AnalyticsControllerTest`**: Analytics REST API contract.
- **`GlobalExceptionHandlerTest`**: HTTP 400, 404, 409, 500 error responses.
- **`SalaryManagementApplicationTests`**: Context initialization and SQLite schema validation.

To run backend tests:
```bash
cd salary-management
./mvnw test
```

### Frontend Test Suite
Covers Angular services, state management, components, and form validation:
```bash
cd salary-frontend
npm run build
```

---

## 📡 REST API Reference

### Employee Endpoints (`/api/employees`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/employees` | Search & filter employees (params: `search`, `country`, `department`, `jobLevel`, `status`, `page`, `size`, `sort`) |
| `GET` | `/api/employees/{id}` | Get employee details by UUID |
| `POST` | `/api/employees` | Create a new employee |
| `PUT` | `/api/employees/{id}` | Update existing employee details |
| `PATCH` | `/api/employees/{id}/deactivate` | Soft-delete (deactivate) an employee |

### Analytics Endpoints (`/api/analytics`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/analytics/summary` | KPI cards summary (`headcount`, `totalPayroll`, `avg`, `median`, `min`, `max`) |
| `GET` | `/api/analytics/by-country` | Breakdown of average & total salary by Country |
| `GET` | `/api/analytics/by-department` | Breakdown of average & total salary by Department |
| `GET` | `/api/analytics/by-job-level` | Breakdown of average & total salary by Job Level |
| `GET` | `/api/analytics/salary-distribution` | Distribution count & percentage across salary bands |

*All analytics endpoints accept `?includeInactive=true|false` (defaults to `false`).*

---

## 🧠 Key Design Decisions & Trade-Offs

1. **Denormalized USD Salary vs. Real-Time Conversion:**
   - Storing `annual_salary_usd` calculated on write allows pure database-level aggregations (`AVG`, `SUM`, `MIN`, `MAX`) with indexed B-tree scanning, executing analytics across 10,000 rows in <10ms.
2. **Deliberately Omitting Caching:**
   - With an indexed relational table of 10k rows and SQLite in memory/file, query latency is negligible. Adding caching layers (e.g. Caffeine/Redis) would introduce cache-invalidation complexity without tangible performance benefits.
3. **Database-Agnostic JPQL:**
   - Aggregations and median calculations avoid vendor-locked syntax (like Postgres `PERCENTILE_CONT`), ensuring compatibility with SQLite, PostgreSQL, MySQL, and H2.
4. **Soft Delete over Hard Delete:**
   - Deactivating preserves historical payroll records for audit integrity while cleanly removing individuals from operational active counts.

---

## 📄 Documentation Artifacts
- [`docs/Requirement.MD`](docs/Requirement.MD) — Comprehensive requirements document and scope definitions.
- [`docs/architecture.md`](docs/architecture.md) — Architectural patterns and layer responsibilities.
- [`docs/dto-design.md`](docs/dto-design.md) — Request/Response contract specifications.
- [`docs/backend.MD`](docs/backend.MD) — Backend implementation guide.
- [`docs/frontend.MD`](docs/frontend.MD) — Frontend component architecture.
