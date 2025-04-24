package com.EmployeePayroll.EmployeePayrollManagement;

import com.EmployeePayroll.EmployeePayrollManagement.Controller.EmployeeController;
import com.EmployeePayroll.EmployeePayrollManagement.DTO.EmployeeDTO;
import com.EmployeePayroll.EmployeePayrollManagement.Entity.Employee;
import com.EmployeePayroll.EmployeePayrollManagement.Exception.EmployeeNotFoundException;
import com.EmployeePayroll.EmployeePayrollManagement.Service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        employee1 = new Employee(1L, "Alice", 2300.45, "IT", "Junior", "Full-Time", LocalDate.of(2023, 1, 1));
        employee2 = new Employee(2L, "Clary", 2100.45, "HR", "HR", "Part-Time", LocalDate.of(2024, 10, 17));
    }

    @Test
    void createEmployeeTest() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(1L, "Alice", 2300.45, "IT", "Junior", "Full-Time", LocalDate.of(2023, 1, 1));
        Employee createdEmployee = new Employee(1L, "Alice", 2300.45, "IT", "Junior", "Full-Time", LocalDate.of(2023, 1, 1));

        when(employeeService.createEmployee(any(Employee.class))).thenReturn(createdEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.designation").value("Junior"));

        verify(employeeService).createEmployee(any(Employee.class));
    }


    @Test
    void getAllEmployeesTest() throws Exception {
        List<Employee> employeeList = Arrays.asList(employee1, employee2);
        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeByIdTest() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee1));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));

        verify(employeeService).getEmployeeById(1L);
    }

    @Test
    void updateEmployeeTest() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(1L, "Alice Updated", 2500.67, "IT", "Senior", "FullTime", LocalDate.of(2023, 1, 1));
        Employee updatedEmployee = new Employee(1L, "Alice Updated", 2500.67, "IT", "Senior", "FullTime", LocalDate.of(2023, 1, 1));

        when(employeeService.updateEmployee(anyLong(), any(Employee.class))).thenReturn(Optional.of(updatedEmployee));

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));

        verify(employeeService).updateEmployee(anyLong(), any(Employee.class));
    }

    @Test
    void calculateTotalPayrollTest() throws Exception {
        double sumTotal = employee1.getSalary() + employee2.getSalary();
        when(employeeService.calculateTotalPayroll()).thenReturn(sumTotal);

        mockMvc.perform(get("/api/employees/payroll"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(sumTotal)));

        verify(employeeService).calculateTotalPayroll();
    }

    @Test
    void getEmployeesGroupedByDepartmentTest() throws Exception {
        Map<String, List<String>> groupedEmployees = new HashMap<>();
        groupedEmployees.put("IT", List.of("Alice"));
        groupedEmployees.put("HR", List.of("Clary"));

        when(employeeService.getEmployeesGroupedByDepartment()).thenReturn(groupedEmployees);

        mockMvc.perform(get("/api/employees/grouped-by-department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.IT[0]").value("Alice"))
                .andExpect(jsonPath("$.HR[0]").value("Clary"));

        verify(employeeService).getEmployeesGroupedByDepartment();
    }

    @Test
    void calculatePayrollByDesignationTest() throws Exception {
        Map<String, Object> payrollMap = new HashMap<>();
        payrollMap.put("Designation/JobTitle", "Manager");
        payrollMap.put("TotalPayroll", 55000.0);

        when(employeeService.calculatePayrollByJobTitle("Manager")).thenReturn(payrollMap);

        mockMvc.perform(get("/api/employees/payroll/job-title/Manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Designation/JobTitle']").value("Manager"))
                .andExpect(jsonPath("$.TotalPayroll").value(55000.0));

        verify(employeeService).calculatePayrollByJobTitle("Manager");
    }

    @Test
    void getEmployeesByDepartment_ValidTest() throws Exception {
        when(employeeService.getEmployeesByDepartment("IT")).thenReturn(List.of("Alice"));

        mockMvc.perform(get("/api/employees/filter-by-department/IT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value("Alice"));

        verify(employeeService).getEmployeesByDepartment("IT");
    }


    @Test
    void getEmployeesByDepartment_NotExistsTest() throws Exception {
        when(employeeService.getEmployeesByDepartment("Marketing"))
                .thenThrow(new EmployeeNotFoundException("No employees found in department- Marketing"));

        mockMvc.perform(get("/api/employees/filter-by-department/Marketing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No employees found in department- Marketing"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(employeeService).getEmployeesByDepartment("Marketing");
    }


    @Test
    void getEmployeesByDepartment_EmptyEmployeeTest() throws Exception {
        when(employeeService.getEmployeesByDepartment("IT"))
                .thenThrow(new EmployeeNotFoundException("No employees found in department- IT"));

        mockMvc.perform(get("/api/employees/filter-by-department/IT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No employees found in department- IT"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(employeeService).getEmployeesByDepartment("IT");
    }

}
