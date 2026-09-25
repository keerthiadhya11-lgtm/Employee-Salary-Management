package com.keerthi.employeesalarymanagement.dto;

import java.time.Instant;

public record EmployeeResponse(Long id, String employeeCode, String firstName, String lastName,
                               String email, String country, String department, String jobTitle,
                               Instant createdAt, Instant updatedAt) {
}
