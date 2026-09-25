CREATE SEQUENCE employees_id_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE salary_history_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE employees (
    id BIGINT PRIMARY KEY DEFAULT nextval('employees_id_seq'),
    employee_code VARCHAR(32) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    country VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    job_title VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_employees_employee_code UNIQUE (employee_code),
    CONSTRAINT uq_employees_email UNIQUE (email)
);

CREATE INDEX idx_employees_first_name ON employees (lower(first_name));
CREATE INDEX idx_employees_last_name ON employees (lower(last_name));
CREATE INDEX idx_employees_country ON employees (country);
CREATE INDEX idx_employees_department ON employees (department);
CREATE INDEX idx_employees_job_title ON employees (job_title);

CREATE TABLE salary_history (
    id BIGINT PRIMARY KEY DEFAULT nextval('salary_history_id_seq'),
    employee_id BIGINT NOT NULL,
    salary_amount NUMERIC(14, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    effective_from DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_salary_history_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    CONSTRAINT ck_salary_history_non_negative CHECK (salary_amount >= 0),
    CONSTRAINT uq_salary_history_employee_effective UNIQUE (employee_id, effective_from)
);

CREATE INDEX idx_salary_history_employee_effective ON salary_history (employee_id, effective_from DESC);
CREATE INDEX idx_salary_history_effective_currency ON salary_history (effective_from, currency);
