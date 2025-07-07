package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        // Clear the database before each test
        mongoTemplate.dropCollection(Employee.class);
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = createTestEmployee("John", "Doe", "Engineering", "Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee);
        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertNotNull(readEmployee);
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertNotNull(updatedEmployee);
        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testFindByName() {
        // Create test employees
        Employee employee1 = createTestEmployee("John", "Smith", "Developer", "Engineering");
        Employee employee2 = createTestEmployee("John", "Smith", "QA", "Tester");
        Employee employee3 = createTestEmployee("Jane", "Doe", "Manager", "HR");

        // Create employees in the system
        restTemplate.postForEntity(employeeUrl, employee1, Employee.class);
        restTemplate.postForEntity(employeeUrl, employee2, Employee.class);
        restTemplate.postForEntity(employeeUrl, employee3, Employee.class);

        // Test search by name
        ResponseEntity<Employee[]> response = restTemplate.getForEntity(
                employeeUrl + "/search-by-name?firstName={firstName}&lastName={lastName}",
                Employee[].class,
                "John",
                "Smith"
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Employee[] foundEmployees = response.getBody();
        assertNotNull(foundEmployees);
        assertEquals(2, foundEmployees.length);

        // Verify both John Smith employees were found
        for (Employee emp : foundEmployees) {
            assertEquals("John", emp.getFirstName());
            assertEquals("Smith", emp.getLastName());
        }

        // Test search with no results
        response = restTemplate.getForEntity(
                employeeUrl + "/search-by-name?firstName={firstName}&lastName={lastName}",
                Employee[].class,
                "NonExistent",
                "Person"
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        foundEmployees = response.getBody();
        assertNotNull(foundEmployees);
        assertEquals(0, foundEmployees.length);
    }

    @Test
    public void testDeleteEmployee() {
        // Create test employee
        Employee testEmployee = createTestEmployee("John", "Doe", "Engineering", "Developer");

        // Create employee
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee);
        assertNotNull(createdEmployee.getEmployeeId());

        // Delete employee
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                employeeIdUrl,
                HttpMethod.DELETE,
                null,
                Void.class,
                createdEmployee.getEmployeeId()
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // Verify employee is deleted by attempting to read it
        ResponseEntity<Employee> readResponse = restTemplate.getForEntity(
                employeeIdUrl,
                Employee.class,
                createdEmployee.getEmployeeId()
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, readResponse.getStatusCode());

        // Test deleting non-existent employee
        ResponseEntity<Void> nonExistentDeleteResponse = restTemplate.exchange(
                employeeIdUrl,
                HttpMethod.DELETE,
                null,
                Void.class,
                "non-existent-id"
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, nonExistentDeleteResponse.getStatusCode());
    }

    /**
     * Creates a test Employee instance with the specified attributes.
     *
     * @param firstName the first name of the employee
     * @param lastName the last name of the employee
     * @param position the position of the employee
     * @param department the department of the employee
     * @return an Employee object populated with the provided attributes
     */
    private Employee createTestEmployee(String firstName, String lastName, String position, String department) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPosition(position);
        employee.setDepartment(department);

        return employee;
    }

    /**
     * Asserts that two Employee objects are equivalent by comparing their first name, last name,
     * department, and position fields.
     *
     * @param expected the expected Employee object
     * @param actual the actual Employee object to be compared
     */
    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}