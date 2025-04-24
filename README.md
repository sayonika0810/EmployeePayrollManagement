# 🧾 Payroll Management System

A **Spring Boot** application that manages employee payroll operations including employee records, salary calculations, department-based filtering, and more. This project uses **JUnit 5**, **Mockito**, and **MockMVC** for testing, and **Postman** for API validation.

---

## 🚀 Features

- Add, update, delete employees  
- Get employees by department or job title  
- Calculate total and average payroll  
- Filter employees hired in the last N months  
- Group employees by department  
- Get top N highest paid employees  
- Proper exception handling and logging

---

## 🛠️ Tech Stack

- Java 17  
- Spring Boot  
- Spring MVC  
- Spring Data JPA  
- H2 Database (In-memory for development)  
- JUnit 5 + Mockito + MockMVC  
- Lombok  
- SLF4J for Logging  
- Postman (for testing)

---

## 📦 Running the Application

### Prerequisites:

- Java 17+
- Maven

### Steps:

```bash
git clone <your-repo-url>
cd EmployeePayrollManagement
mvn spring-boot:run
```

The backend will run at: `http://localhost:8080`

---

## 🔗 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/employees` | Add a new employee |
| `GET` | `/api/employees` | Get all employees |
| `GET` | `/api/employees/{id}` | Get employee by ID |
| `PUT` | `/api/employees/{id}` | Update employee by ID |
| `DELETE` | `/api/employees/{id}` | Delete employee by ID |
| `GET` | `/api/employees/filter-by-department/{department}` | Get employees by department |
| `GET` | `/api/employees/departments/grouped` | Get employees grouped by department |
| `GET` | `/api/employees/top-salaries/{n}` | Get top N highest paid employees |
| `GET` | `/api/employees/payroll/total` | Calculate total payroll |
| `GET` | `/api/employees/payroll/average/{department}` | Average salary by department |
| `GET` | `/api/employees/payroll/by-job-title?jobTitle=Senior` | Payroll by job title |
| `GET` | `/api/employees/hired-in-last/{months}` | Employees hired in last N months |

---

## 🧪 Testing

Run unit tests with:

```bash
mvn test
```

Includes full coverage for:
- Controller Layer  
- Service Layer  
- Exception Handling

---

## 📬 Postman Collection

You can use the provided Postman collection to test all endpoints easily.  
👉 _[Optional: Add link or export file here]_

---

## 📝 Author

**Your Name**  
[GitHub](https://github.com/yourusername) | [LinkedIn](https://linkedin.com/in/yourprofile)

