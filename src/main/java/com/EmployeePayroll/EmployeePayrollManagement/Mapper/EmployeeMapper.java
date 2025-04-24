package com.EmployeePayroll.EmployeePayrollManagement.Mapper;

import com.EmployeePayroll.EmployeePayrollManagement.DTO.EmployeeDTO;
import com.EmployeePayroll.EmployeePayrollManagement.Entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    // Converting Entity to DTO
    public static EmployeeDTO toDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getEmpId(),
                employee.getName(),
                employee.getSalary(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getEmploymentType(),
                employee.getHireDate()
        );
    }

    // Converting DTO to Entity
    public static Employee toEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setEmpId(employeeDTO.getEmpId());
        employee.setName(employeeDTO.getName());
        employee.setSalary(employeeDTO.getSalary());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setDesignation(employeeDTO.getDesignation());
        employee.setEmploymentType(employeeDTO.getEmploymentType());
        employee.setHireDate(employeeDTO.getHireDate());
        return employee;
    }
}
