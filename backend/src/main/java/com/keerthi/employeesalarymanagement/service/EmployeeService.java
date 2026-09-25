package com.keerthi.employeesalarymanagement.service;

import com.keerthi.employeesalarymanagement.dto.EmployeeRequest;
import com.keerthi.employeesalarymanagement.dto.EmployeeResponse;
import com.keerthi.employeesalarymanagement.entity.Employee;
import com.keerthi.employeesalarymanagement.exception.ConflictException;
import com.keerthi.employeesalarymanagement.exception.ResourceNotFoundException;
import com.keerthi.employeesalarymanagement.repository.EmployeeRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employees;

    public EmployeeService(EmployeeRepository employees) {
        this.employees = employees;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        String code = normalizeCode(request.employeeCode());
        String email = normalizeEmail(request.email());
        if (employees.existsByEmployeeCodeIgnoreCase(code)) {
            throw new ConflictException("Employee code already exists: " + code);
        }
        if (employees.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already exists: " + email);
        }
        Employee employee = employees.save(new Employee(code, clean(request.firstName()), clean(request.lastName()), email,
                clean(request.country()), clean(request.department()), clean(request.jobTitle())));
        return toResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return toResponse(findEmployee(id));
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        String code = normalizeCode(request.employeeCode());
        String email = normalizeEmail(request.email());
        if (employees.existsByEmployeeCodeIgnoreCaseAndIdNot(code, id)) {
            throw new ConflictException("Employee code already exists: " + code);
        }
        if (employees.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException("Email already exists: " + email);
        }
        employee.updateDetails(code, clean(request.firstName()), clean(request.lastName()), email,
                clean(request.country()), clean(request.department()), clean(request.jobTitle()));
        return toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> search(String search, String country, String department, String jobTitle,
                                         Pageable pageable) {
        Specification<Employee> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(search)) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                List<Predicate> matches = new ArrayList<>();
                matches.add(builder.like(builder.lower(root.get("employeeCode")), pattern));
                matches.add(builder.like(builder.lower(root.get("firstName")), pattern));
                matches.add(builder.like(builder.lower(root.get("lastName")), pattern));
                matches.add(builder.like(builder.lower(root.get("email")), pattern));
                try {
                    matches.add(builder.equal(root.get("id"), Long.parseLong(search.trim())));
                } catch (NumberFormatException ignored) {
                    // A non-numeric query still searches code, names and email.
                }
                predicates.add(builder.or(matches.toArray(Predicate[]::new)));
            }
            addExactFilter(predicates, root.get("country"), country, builder);
            addExactFilter(predicates, root.get("department"), department, builder);
            addExactFilter(predicates, root.get("jobTitle"), jobTitle, builder);
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return employees.findAll(specification, pageable).map(EmployeeService::toResponse);
    }

    private Employee findEmployee(Long id) {
        return employees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private static void addExactFilter(List<Predicate> predicates, jakarta.persistence.criteria.Path<String> field,
                                       String value, jakarta.persistence.criteria.CriteriaBuilder builder) {
        if (hasText(value)) {
            predicates.add(builder.equal(builder.lower(field), value.trim().toLowerCase(Locale.ROOT)));
        }
    }

    private static boolean hasText(String value) { return value != null && !value.isBlank(); }
    private static String clean(String value) { return value.trim(); }
    private static String normalizeCode(String value) { return clean(value).toUpperCase(Locale.ROOT); }
    private static String normalizeEmail(String value) { return clean(value).toLowerCase(Locale.ROOT); }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getEmployeeCode(), employee.getFirstName(),
                employee.getLastName(), employee.getEmail(), employee.getCountry(), employee.getDepartment(),
                employee.getJobTitle(), employee.getCreatedAt(), employee.getUpdatedAt());
    }
}
