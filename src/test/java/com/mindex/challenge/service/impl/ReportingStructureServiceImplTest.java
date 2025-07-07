package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    private String reportingStructureUrl;
    private String employeeUrl;
    private String employeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach  // Changed from @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reporting-structure/{id}";
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    }

    @AfterEach
    public void cleanup() {
        mongoTemplate.dropCollection(Employee.class);
    }

    @Test
    public void testGetReportingStructure_NoReports() {
        LOG.debug("Testing getReportingStructure_NoReports");

        // Create test employee with no reports
        Employee createdEmployee = createTestEmployee("John", "Doe", "Engineering", "Developer");
        assertNotNull(createdEmployee);

        // Test reporting structure
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertEquals(0, reportingStructure.getNumberOfReports());
        assertEquals(createdEmployee.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
    }

    @Test
    public void testGetReportingStructure_WithDirectReports() {
        LOG.debug("Testing getReportingStructure_WithDirectReports");

        // Create employees for testing
        Employee employee2 = createTestEmployee("Bob", "Jones", "Developer", "Engineering");
        Employee employee3 = createTestEmployee("Charlie", "Brown", "Developer", "Engineering");

        // Create manager with direct reports
        Employee createdManager = createTestEmployeeWithDirectReports("Alice", "Smith", "Manager", "Engineering", Arrays.asList(employee2, employee3));
        assertNotNull(createdManager);
        assertNotNull(createdManager.getEmployeeId());

        // Update manager to ensure direct reports are properly linked
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Employee updatedManager = restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<>(createdManager, headers),
                Employee.class,
                createdManager.getEmployeeId()).getBody();

        // Test reporting structure
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, updatedManager.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertEquals(2, reportingStructure.getNumberOfReports());
        assertEquals(updatedManager.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
    }

    @Test
    public void testGetReportingStructure_ComplexHierarchy() {
        LOG.debug("Testing getReportingStructure_ComplexHierarchy");

        // Create all employees first
        Employee dev1 = createTestEmployee("Dev", "One", "Developer", "Engineering");
        Employee dev2 = createTestEmployee("Dev", "Two", "Developer", "Engineering");
        Employee sales1 = createTestEmployee("Sales", "One", "Sales Rep", "Sales");

        // Create and save manager1 with direct reports
        Employee savedManager1 = createTestEmployeeWithDirectReports("Manager", "One", "Manager", "Engineering", Arrays.asList(dev1, dev2));

        // Create and save manager2 with direct reports
        Employee savedManager2 = createTestEmployeeWithDirectReports("Manager", "Two", "Manager", "Sales", Arrays.asList(sales1));

        // Create CEO with both managers as direct reports
        Employee savedCeo = createTestEmployeeWithDirectReports("CEO", "Person", "CEO", "Executive", Arrays.asList(savedManager1, savedManager2));
        assertNotNull(savedCeo);

        // Update CEO to ensure direct reports are properly linked
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Employee updatedCeo = restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<>(savedCeo, headers),
                Employee.class,
                savedCeo.getEmployeeId()).getBody();

        // Test reporting structure
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class, updatedCeo.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertEquals(5, reportingStructure.getNumberOfReports());
        assertEquals(updatedCeo.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
    }

    /**
     * Creates a test instance of an Employee object with the provided attributes but does not make
     * any network or database calls to persist the employee.
     *
     * @param firstName the first name of the employee
     * @param lastName the last name of the employee
     * @param position the position or title of the employee
     * @param department the department in which the employee works
     * @return an Employee object with the specified attributes
     */
    private Employee createTestEmployeeNoPost(String firstName, String lastName, String position, String department) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPosition(position);
        employee.setDepartment(department);

        return employee;
    }

    /**
     * Creates and persists a test instance of an Employee object using the provided attributes.
     * Sends a POST request to persist the Employee details and returns the created Employee object.
     *
     * @param firstName the first name of the employee
     * @param lastName the last name of the employee
     * @param position the position or title of the employee
     * @param department the department in which the employee works
     * @return the persisted Employee object with the specified attributes
     */
    private Employee createTestEmployee(String firstName, String lastName, String position, String department) {
        return restTemplate.postForEntity(employeeUrl,
                createTestEmployeeNoPost(firstName, lastName, position, department),
                Employee.class).getBody();
    }

    /**
     * Creates and persists a test instance of an Employee object with the provided attributes and direct reports.
     * Sends a POST request to save the Employee details and returns the created Employee object.
     *
     * @param firstName the first name of the employee
     * @param lastName the last name of the employee
     * @param position the position or title of the employee
     * @param department the department in which the employee works
     * @param directReports the list of direct reports associated with this employee
     * @return the persisted Employee object with the specified attributes and direct reports
     */
    private Employee createTestEmployeeWithDirectReports(String firstName, String lastName, String position, String department, List<Employee> directReports) {
        Employee employee = createTestEmployeeNoPost(firstName, lastName, position, department);
        employee.setDirectReports(directReports);
        return restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
    }
}