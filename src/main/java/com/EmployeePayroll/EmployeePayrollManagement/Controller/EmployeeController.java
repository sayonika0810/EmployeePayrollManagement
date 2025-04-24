package com.EmployeePayroll.EmployeePayrollManagement.Controller;

import com.EmployeePayroll.EmployeePayrollManagement.DTO.EmployeeDTO;
import com.EmployeePayroll.EmployeePayrollManagement.Entity.Employee;
import com.EmployeePayroll.EmployeePayrollManagement.Mapper.EmployeeMapper;
import com.EmployeePayroll.EmployeePayrollManagement.Service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j // Enables logging
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("Received request: POST /api/employees with data: {}", employeeDTO);
        Employee employee = EmployeeMapper.toEntity(employeeDTO);
        Employee savedEmployee = employeeService.createEmployee(employee);
        EmployeeDTO response = EmployeeMapper.toDTO(savedEmployee);
        log.info("Employee created successfully: {}", response);
        return response;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        log.info("Received request: GET /api/employees");
        List<EmployeeDTO> employees = employeeService.getAllEmployees().stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Returning {} employees", employees.size());
        return employees;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id") Long id) {
        log.info("Received request: GET /api/employees/{}", id);
        return employeeService.getEmployeeById(id)
                .map(employee -> {
                    EmployeeDTO response = EmployeeMapper.toDTO(employee);
                    log.info("Employee found: {}", response);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("Employee with ID {} not found!", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeDTO employeeDTO) {
        log.info("Received request: PUT /api/employees/{} with data: {}", id, employeeDTO);
        Employee updatedEmployeeDetails = EmployeeMapper.toEntity(employeeDTO);
        return employeeService.updateEmployee(id, updatedEmployeeDetails)
                .map(employee -> {
                    EmployeeDTO response = EmployeeMapper.toDTO(employee);
                    log.info("Employee updated successfully: {}", response);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("Employee with ID {} not found for update!", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        log.info("Received request: DELETE /api/employees/{}", id);
        return employeeService.deleteEmployee(id);
    }

    @GetMapping("/payroll")
    public double calculateTotalPayroll() {
        log.info("Received request: GET /api/employees/payroll");
        double payroll = employeeService.calculateTotalPayroll();
        log.info("Total payroll calculated: {}", payroll);
        return payroll;
    }

    @GetMapping("/department/{departmentName}/average-salary")
    public double calculateAverageSalaryByDepartment(@PathVariable String departmentName) {
        log.info("Received request: GET /api/employees/department/{}/average-salary", departmentName);
        double avgSalary = employeeService.calculateAverageSalaryByDepartment(departmentName);
        log.info("Average salary for department {}: {}", departmentName, avgSalary);
        return avgSalary;
    }

    @GetMapping("/grouped-by-department")
    public Map<String, List<String>> getEmployeesGroupedByDepartment() {
        log.info("Received request: GET /api/employees/grouped-by-department");
        return employeeService.getEmployeesGroupedByDepartment();
    }

    @GetMapping("/top-salaries/{n}")
    public ResponseEntity<List<EmployeeDTO>> getTopNHighestPaidEmployees(@PathVariable int n) {
        log.info("Received request: GET /api/employees/top-salaries/{}", n);
        List<EmployeeDTO> employeeDTOs = employeeService.getTopNHighestPaidEmployees(n)
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Returning top {} highest-paid employees", n);
        return ResponseEntity.ok(employeeDTOs);
    }

    @GetMapping("/payroll/job-title/{jobTitle}")
    public Map<String, Object> calculatePayrollByDesignation(@PathVariable String jobTitle) {
        log.info("Received request: GET /api/employees/payroll/job-title/{}", jobTitle);
        return employeeService.calculatePayrollByJobTitle(jobTitle);
    }

    @GetMapping("/hired-in-last/{months}")
    public List<Employee> findEmployeesHiredInLastNMonths(@PathVariable int months) {
        log.info("Received request: GET /api/employees/hired-in-last/{}", months);
        return employeeService.findEmployeesHiredInLastNMonths(months);
    }

    @GetMapping("/filter-by-department/{department}")
    public List<String> getEmployeesByDepartment(@PathVariable String department){
        log.info("Received request: GET/api/employees/filter-by-department/{}",department);
        return employeeService.getEmployeesByDepartment(department);
    }
}
