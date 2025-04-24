package com.EmployeePayroll.EmployeePayrollManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empId;
    private String name;
    private double salary;
    @Column(name="departmentTitle")
    private String department;
    private String designation;
    private String employmentType;
    private LocalDate hireDate;
}
