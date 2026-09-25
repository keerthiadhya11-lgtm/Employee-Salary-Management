package com.keerthi.employeesalarymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "salary_history", uniqueConstraints = @UniqueConstraint(
        name = "uq_salary_history_employee_effective", columnNames = {"employee_id", "effective_from"}))
public class SalaryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_history_seq")
    @SequenceGenerator(name = "salary_history_seq", sequenceName = "salary_history_id_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "salary_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal salaryAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SalaryHistory() {
    }

    public SalaryHistory(BigDecimal salaryAmount, String currency, LocalDate effectiveFrom) {
        this.salaryAmount = salaryAmount;
        this.currency = currency;
        this.effectiveFrom = effectiveFrom;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    void setEmployee(Employee employee) { this.employee = employee; }

    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public BigDecimal getSalaryAmount() { return salaryAmount; }
    public String getCurrency() { return currency; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public Instant getCreatedAt() { return createdAt; }
}
