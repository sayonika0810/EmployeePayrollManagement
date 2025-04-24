package com.EmployeePayroll.EmployeePayrollManagement.Service;

import com.EmployeePayroll.EmployeePayrollManagement.Entity.Employee;
import com.EmployeePayroll.EmployeePayrollManagement.Exception.EmployeeNotFoundException;
import com.EmployeePayroll.EmployeePayrollManagement.Repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class); // Creating the logger

    private final EmployeeRepository employeeRepository;
    private final Map<String, Double> baseSalaryMap = new HashMap<>();

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;

        // Base salary initialization with proper validation
        baseSalaryMap.put("Manager", 30000.00);
        baseSalaryMap.put("HR", 20000.00);
        baseSalaryMap.put("JuniorEngineer", 15000.00);
        baseSalaryMap.put("SeniorEngineer", 30000.00);
        baseSalaryMap.put("Tester", 25000.00);
        baseSalaryMap.put("Analyst", 25000.00);
    }

    public Employee createEmployee(Employee employee) {
        logger.info("Creating a new employee: {}", employee.getName());
        Employee createdEmployee = employeeRepository.save(employee);
        logger.info("Employee created with ID: {}", createdEmployee.getEmpId());
        return createdEmployee;
    }

    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees...");
        List<Employee> employees = employeeRepository.findAll();
        logger.info("Found {} employees.", employees.size());
        return employees;
    }

    public Optional<Employee> getEmployeeById(Long empId) {
        logger.info("Fetching employee with ID: {}", empId);
        return Optional.ofNullable(employeeRepository.findById(empId)
                .orElseThrow(() -> {
                    logger.error("Employee with ID {} not found.", empId);
                    return new EmployeeNotFoundException("Employee with ID " + empId + " not found");
                }));
    }

    public Optional<Employee> updateEmployee(Long id, Employee updatedEmployeeDetails) {
        logger.info("Updating employee with ID: {}", id);
        return Optional.ofNullable(employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(updatedEmployeeDetails.getName());
                    employee.setSalary(updatedEmployeeDetails.getSalary());
                    employee.setDepartment(updatedEmployeeDetails.getDepartment());
                    employee.setDesignation(updatedEmployeeDetails.getDesignation());
                    employee.setEmploymentType(updatedEmployeeDetails.getEmploymentType());
                    logger.info("Employee updated with ID: {}", id);
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> {
                    logger.error("Employee with ID {} not found for update.", id);
                    return new EmployeeNotFoundException("Employee with ID " + id + " not found");
                }));
    }

    public ResponseEntity<Void> deleteEmployee(Long id) {
        logger.info("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Employee with ID {} not found for deletion.", id);
                    return new EmployeeNotFoundException("Employee with ID " + id + " not found");
                });
        employeeRepository.deleteById(id);
        logger.info("Employee with ID {} successfully deleted.", id);
        return ResponseEntity.noContent().build();
    }

    //TASK 4: Calculate total payroll
    public double calculateTotalPayroll() {
        logger.info("Calculating total payroll...");
        List<Employee> employees = employeeRepository.findAll();
        double totalPayroll = 0.0;

        for (Employee employee : employees) {
            String role = employee.getDesignation();
            Double baseSalary = baseSalaryMap.get(role);
            if (baseSalary != null) {
                double salaryWithBonuses = baseSalary + employee.getSalary();
                totalPayroll += salaryWithBonuses;
            } else {
                logger.error("Base salary not found for role: {}", role);
                throw new EmployeeNotFoundException("Salary base not found for role: " + role);
            }
        }

        logger.info("Total payroll calculated: {}", totalPayroll);
        return totalPayroll;
    }

    //TASK 5: Calculate average salary by department
    public double calculateAverageSalaryByDepartment(String departmentName) {
        logger.info("Calculating average salary for department: {}", departmentName);
        List<Employee> employees = employeeRepository.findAll()
                .stream().filter(employee -> employee.getDepartment().equals(departmentName))
                .collect(Collectors.toList());

        if (employees.isEmpty()) {
            logger.error("No employees found in department: {}", departmentName);
            throw new EmployeeNotFoundException("No Employee Present in this Department");
        }

        double avgSalary = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
        logger.info("Average salary for department {}: {}", departmentName, avgSalary);
        return avgSalary;
    }

    //TASK 6: Get employees grouped by department
    public Map<String, List<String>> getEmployeesGroupedByDepartment() {
        logger.info("Grouping employees by department...");
        Map<String, List<String>> employeesGrouped = employeeRepository.findAll()
                .stream().collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.mapping(Employee::getName, Collectors.toList())));

        if (employeesGrouped.isEmpty()) {
            logger.error("No employees found to group by department.");
            throw new EmployeeNotFoundException("No Employees found in any Department");
        }

        logger.info("Employees grouped by department successfully.");
        return employeesGrouped;
    }

    //TASK 7: Get top N highest-paid employees
    public List<Employee> getTopNHighestPaidEmployees(int n) {
        logger.info("Fetching top {} highest-paid employees...", n);
        List<Employee> topEmployees = employeeRepository.findAll().stream()
                .sorted(Comparator.comparing(Employee::getSalary, Comparator.reverseOrder()))
                .limit(n)
                .collect(Collectors.toList());
        logger.info("Top {} highest-paid employees fetched.", n);
        return topEmployees;
    }

    //TASK 8: Calculate payroll by job title (designation)
    public Map<String, Object> calculatePayrollByJobTitle(String jobTitle) {
        logger.info("Calculating payroll by job title: {}", jobTitle);
        if (jobTitle == null || jobTitle.trim().isEmpty()) {
            logger.error("Job title is empty or null.");
            throw new IllegalArgumentException("Job title must not be null or empty.");
        }

        List<Employee> employees = employeeRepository.findAll()
                .stream()
                .filter(employee -> employee.getDesignation().equals(jobTitle))
                .collect(Collectors.toList());

        if (employees.isEmpty()) {
            logger.error("No employees found with the designation: {}", jobTitle);
            throw new EmployeeNotFoundException("No employee found with the designation: " + jobTitle);
        }

        Double baseSalary = baseSalaryMap.get(jobTitle);
        if (baseSalary == null) {
            logger.error("No base salary defined for job title: {}", jobTitle);
            throw new EmployeeNotFoundException("No base salary defined for job title: " + jobTitle);
        }

        double totalPayroll = employees.stream()
                .mapToDouble(employee -> baseSalary + employee.getSalary())
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("Designation/JobTitle", jobTitle);
        result.put("Employees", employees);

        logger.info("Payroll calculation for job title {} completed. Total Payroll: {}", jobTitle, totalPayroll);
        return result;
    }

    //TASK 9: Find employees hired in the last N months
    public List<Employee> findEmployeesHiredInLastNMonths(int months) {
        logger.info("Fetching employees hired in the last {} months.", months);
        LocalDate currentDate = LocalDate.now();
        LocalDate cutoffDate = currentDate.minusMonths(months);

        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getHireDate() != null)
                .filter(employee -> employee.getHireDate().isAfter(cutoffDate))
                .collect(Collectors.toList());

        logger.info("Found {} employees hired in the last {} months.", employees.size(), months);
        return employees;
    }


    //Extra from JML4:Filtering employee by department
    public List<String> getEmployeesByDepartment(String department){
        logger.info("Fetching employees from the department {}.",department);
        List<Employee> employees=employeeRepository.findAll();
        List<String> result=employees.stream()
                .filter(employee -> employee.getDepartment().equalsIgnoreCase(department))
                .map(Employee::getName)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            logger.error("No employees found in the department: {}", department);
            throw new EmployeeNotFoundException("No employee found in the Department: " + department);
        }
        logger.info("Found {} employees from the {} department",result.size(),department);
        return result;
    }
}
