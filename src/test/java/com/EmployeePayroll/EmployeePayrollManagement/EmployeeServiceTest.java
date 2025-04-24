package com.EmployeePayroll.EmployeePayrollManagement;

import com.EmployeePayroll.EmployeePayrollManagement.Entity.Employee;
import com.EmployeePayroll.EmployeePayrollManagement.Exception.EmployeeNotFoundException;
import com.EmployeePayroll.EmployeePayrollManagement.Repository.EmployeeRepository;
import com.EmployeePayroll.EmployeePayrollManagement.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employee1 = new Employee(1L, "Alice", 2300.45, "IT", "JuniorEngineer", "Full-Time", LocalDate.of(2023, 1, 1));
        employee2 = new Employee(2L, "Clary", 2100.45, "HR", "HR", "Part-Time", LocalDate.of(2024, 10, 17));
    }

    @Test
    void createEmployeeTest() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);
        Employee createdEmployee = employeeService.createEmployee(employee1);
        assertNotNull(createdEmployee);
        assertEquals("Alice", createdEmployee.getName());
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    void getAllEmployeesTest() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeRepository.findAll()).thenReturn(employees);
        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(2, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getEmployeeByIdTest() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        Optional<Employee> result = employeeService.getEmployeeById(1L);
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void updateEmployeeTest() {
        Employee updatedEmployee = new Employee(1L, "Alice(Update)", 2700.88, "IT", "Senior", "Full-Time", LocalDate.of(2023, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        Optional<Employee> result = employeeService.updateEmployee(1L, updatedEmployee);
        assertTrue(result.isPresent());
        assertEquals("Alice(Update)", result.get().getName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void deleteEmployeeTest() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        doNothing().when(employeeRepository).deleteById(1L);
        employeeService.deleteEmployee(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void calculateTotalPayrollTest() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        double totalPayroll = employeeService.calculateTotalPayroll();
        assertTrue(totalPayroll > 0);
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void calculateAverageSalaryByDepartmentTest() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        double avgSalary = employeeService.calculateAverageSalaryByDepartment("IT");
        assertTrue(avgSalary > 0);
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testCalculateAverageSalaryByDepartment_NoEmployees() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.calculateAverageSalaryByDepartment("Marketing");
        });
        assertEquals("No Employee Present in this Department", exception.getMessage());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getEmployeesGroupedByDepartmentTest() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        Map<String, List<String>> result = employeeService.getEmployeesGroupedByDepartment();
        assertEquals(2, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getTopNHighestPaidEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        List<Employee> result = employeeService.getTopNHighestPaidEmployees(1);
        assertEquals(1, result.size());
        assertEquals(employee1, result.get(0));
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testCalculatePayrollByJobTitle_Valid() {
        String jobTitle = "SeniorEngineer";
        employee1.setDesignation(jobTitle);
        employee1.setSalary(5000.0);

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1));
        Map<String, Object> result = employeeService.calculatePayrollByJobTitle(jobTitle);

        assertEquals(jobTitle, result.get("Designation/JobTitle"));
        assertEquals(Arrays.asList(employee1), result.get("Employees"));
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testCalculatePayrollByJobTitle_NullJobTitle() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.calculatePayrollByJobTitle(null);
        });
        assertEquals("Job title must not be null or empty.", exception.getMessage());
    }

    @Test
    void testCalculatePayrollByJobTitle_EmptyJobTitle() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.calculatePayrollByJobTitle("   ");
        });
        assertEquals("Job title must not be null or empty.", exception.getMessage());
    }

    @Test
    void testCalculatePayrollByJobTitle_NoEmployeesFound() {
        String jobTitle = "Marketing";
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.calculatePayrollByJobTitle(jobTitle);
        });

        assertEquals("No employee found with the designation: Marketing", exception.getMessage());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testCalculatePayrollByJobTitle_NoBaseSalaryDefined() {
        String jobTitle = "Intern";
        employee1.setDesignation(jobTitle);

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1));
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.calculatePayrollByJobTitle(jobTitle);
        });
        assertEquals("No base salary defined for job title: Intern", exception.getMessage());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testFindEmployeesHiredInLastNMonths_Valid() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        List<Employee> result = employeeService.findEmployeesHiredInLastNMonths(10);
        assertEquals(1, result.size());
        assertEquals(employee2, result.get(0));
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testFindEmployeesHiredInLastNMonths_NoEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1));
        List<Employee> result = employeeService.findEmployeesHiredInLastNMonths(3);
        assertEquals(0, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test//returns list of employees
    void getEmployeesByDepartmentTest_Valid(){
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1,employee2));
        List<String> result=employeeService.getEmployeesByDepartment("IT");
        assertEquals(1, result.size());
        assertTrue(result.contains("Alice"));
        verify(employeeRepository,times(1)).findAll();
    }
    @Test
    void getEmployeesByDepartmentTest_NotExists() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        Exception ex = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeesByDepartment("Marketing"));
        assertEquals("No employee found in the Department: Marketing", ex.getMessage()); // <- updated
        verify(employeeRepository, times(1)).findAll();
    }

    @Test//when employee list is empty
    void getEmployeesByDepartmentTest_EmptyEmployee() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        Exception ex = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeesByDepartment("IT"));
        assertEquals("No employee found in the Department: IT", ex.getMessage());
        verify(employeeRepository, times(1)).findAll();
    }



}
