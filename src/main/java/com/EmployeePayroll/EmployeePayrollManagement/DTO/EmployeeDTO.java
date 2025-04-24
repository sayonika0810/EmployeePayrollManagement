package com.EmployeePayroll.EmployeePayrollManagement.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeDTO {
    private Long empId;
    private String name;
    private double salary;
    private String department;
    private String designation;
    private String employmentType;
    private LocalDate hireDate;
}
