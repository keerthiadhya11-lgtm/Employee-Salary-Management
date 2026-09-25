# Employee Salary Management — Requirements

## Goal

Give ACME’s HR team a web application for maintaining employee salary records and answering common questions about the organization’s pay.

## Primary user persona

**HR Manager:** maintains employee details and salary changes, and reviews workforce and salary summaries across countries and departments.

## Problem statement

HR currently manages salary information for 10,000 employees in spreadsheets. Keeping records consistent is tedious, salary changes can obscure earlier values, and producing reliable summaries takes manual work.

## In scope

- Angular web application using Angular Material.
- Java 21 / Spring Boot REST backend, PostgreSQL persistence, Spring Data JPA / Hibernate, Flyway migrations, Maven and Docker.
- A modular monolith with employee, salary-history and insights responsibilities.

## Functional features

- Create, view, update, search and filter employees by employee ID/code, name, email, country, department and job title.
- List employees with server-side pagination; combine search terms, filters and pagination.
- Add effective-dated salary records and view salary history without replacing earlier records.
- Show an employee’s current salary using the latest effective date that applies today.
- Show total employee count; salary average/minimum/maximum by currency; employee counts by country and department; and current-salary statistics by country and department, grouped by currency.
- Never add or compare amounts in different currencies without conversion.
- Validate required fields and amounts, and return consistent API errors for invalid, missing and conflicting requests.
- Provide deterministic demo data for exactly 10,000 employees, with salary records, for local assessment use.

## Non-functional requirements

- Support the assessment dataset of approximately 10,000 employees through database-side search, filtering, aggregation and pagination.
- Store salary values as decimal monetary data (`BigDecimal` in Java and `NUMERIC` in PostgreSQL).
- Preserve salary history and employee/salary referential integrity.
- Keep the code modular, testable and runnable with Docker; document the REST API.
- Provide a polished README with setup, seed, test, Swagger and Docker deployment instructions. The target hosting platform is not specified; deployment readiness means the documented Docker setup is repeatable.

## Explicitly out of scope

- Authentication and authorization, employee self-service, payroll execution, tax calculation, currency conversion, and microservices.
- Salary-range bucket reporting until range boundaries are agreed and the backend exposes an aggregate endpoint for them.

These capabilities are deliberately excluded because the assessment focuses on HR-managed employee and salary records and basic insights. Authentication requires an identity and role model; payroll and tax calculations require jurisdiction-specific rules; currency conversion needs an exchange-rate source and valuation policy. The existing backend does not expose salary-range bucket counts, and the assessment does not supply currency-specific band boundaries, so the frontend does not derive or invent that report. None of these capabilities is necessary to demonstrate the current employee and salary-history workflow. The application is not intended to process payroll or calculate comparable converted pay.

## Success criteria

- An HR Manager can maintain employees and append salary changes while retaining previous values.
- Employee lists are paginated and filters can be combined.
- Current salary and history follow effective dates.
- Dashboard calculations are database-backed and currency-aware.
- Validation, not-found and conflict cases return documented HTTP statuses and error bodies.
- The application migrates a PostgreSQL database, runs in Docker, and loads exactly 10,000 deterministic demo employees when explicitly seeded into an empty database.
- Focused backend and Angular tests cover the main business behavior.
- A 3–5 minute demo shows the dashboard, 10,000 employees, search/filter, employee details and salary history, a salary update, refreshed insights, Swagger and passing tests.

## Assumptions to confirm

- The assessment does not say whether salary amounts are annual, monthly or another period. For requested averages to be meaningful, assume amounts share one period; if they can differ, add a period field and group reports by it before implementation.
- Salary currency is represented by a currency code. The accepted code standard and precision/scale should be fixed before implementation.
- If salary-range reporting is added later, agree bands per currency and expose database-calculated counts through the backend; do not imply equal numeric ranges are comparable across currencies.
