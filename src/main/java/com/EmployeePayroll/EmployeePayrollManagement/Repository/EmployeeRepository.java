package com.EmployeePayroll.EmployeePayrollManagement.Repository;

import com.EmployeePayroll.EmployeePayrollManagement.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
