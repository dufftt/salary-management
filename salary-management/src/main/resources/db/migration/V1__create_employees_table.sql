CREATE TABLE employees (
    id                  TEXT PRIMARY KEY,
    employee_id         VARCHAR(50)     NOT NULL,
    full_name           VARCHAR(150)    NOT NULL,
    email               VARCHAR(150)    NOT NULL,
    department          VARCHAR(50)     NOT NULL,
    job_title           VARCHAR(150)    NOT NULL,
    job_level           VARCHAR(50)     NOT NULL,
    country             VARCHAR(50)     NOT NULL,
    currency            CHAR(3)         NOT NULL,
    annual_salary       NUMERIC(12,2)   NOT NULL,
    annual_salary_usd   NUMERIC(12,2)   NOT NULL,
    status              VARCHAR(20)     NOT NULL,
    date_of_joining     DATE            NOT NULL,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP       NOT NULL,

    CONSTRAINT uk_employees_employee_id UNIQUE (employee_id),
    CONSTRAINT uk_employees_email UNIQUE (email)
);

CREATE INDEX idx_employees_country ON employees (country);
CREATE INDEX idx_employees_department ON employees (department);
CREATE INDEX idx_employees_job_level ON employees (job_level);
CREATE INDEX idx_employees_status ON employees (status);
CREATE INDEX idx_employees_annual_salary ON employees (annual_salary);
CREATE INDEX idx_employees_annual_salary_usd ON employees (annual_salary_usd);