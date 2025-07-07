package com.mindex.challenge;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompensationServiceImplTest {

    @Autowired
    private CompensationService compensationService;

    @Test
    void testCreateRead() {
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Lennon");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Development Manager");

        Compensation compensation = new Compensation();
        compensation.setEmployee(testEmployee);
        compensation.setSalary(75000.00);
        compensation.setEffectiveDate(LocalDate.of(2024, 1, 1));

        // Create checks
        Compensation createdCompensation = compensationService.create(compensation);
        assertNotNull(createdCompensation);
        assertCompensationEquivalence(compensation, createdCompensation);

        // Read checks
        Compensation readCompensation = compensationService.read(testEmployee.getEmployeeId());
        assertNotNull(readCompensation);
        assertCompensationEquivalence(compensation, readCompensation);
    }

    @Test
    void testReadNonExistentEmployee() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            compensationService.read("invalid-id");
        });
        assertTrue(exception.getMessage().contains("No compensation found"));
    }

    @Test
    void testCreateWithInvalidEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("invalid-id");

        Compensation compensation = new Compensation();
        compensation.setEmployee(testEmployee);
        compensation.setSalary(75000.00);
        compensation.setEffectiveDate(LocalDate.of(2024, 1, 1));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            compensationService.create(compensation);
        });
        assertTrue(exception.getMessage().contains("Invalid employeeId"));
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary(), 0.001);
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}