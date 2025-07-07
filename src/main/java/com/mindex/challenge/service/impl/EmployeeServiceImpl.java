package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        //Adding a duplicate entry validation
        Employee existingEmployee = employeeRepository.findFirstByEmployeeId(employee.getEmployeeId());
        if (existingEmployee != null) {
            throw new RuntimeException("Employee already exists with id: " + employee.getEmployeeId());
        }
        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findFirstByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> findByName(String firstName, String lastName) {
        LOG.debug("Finding employees with firstName: [{}] and lastName: [{}]", firstName, lastName);

        List<Employee> employees = employeeRepository.findByFirstNameAndLastName(firstName, lastName);
        if (employees.isEmpty()) {
            LOG.debug("No employees found with the given name");
        }

        return employees;
    }

    @Override
    public void deleteEmployee(String employeeId) {
        LOG.debug("Deleting employee with id [{}]", employeeId);

        // Verify employee exists before deletion
        Employee employee = employeeRepository.findFirstByEmployeeId(employeeId);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        employeeRepository.deleteByEmployeeId(employeeId);
    }
}
