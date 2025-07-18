
package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        // Ensure the employee exists and is fully populated
        Employee employee = employeeRepository.findFirstByEmployeeId(compensation.getEmployee().getEmployeeId());
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId");
        }

        // Check if compensation already exists
        Compensation existingCompensation = compensationRepository.findByEmployee_EmployeeId(compensation.getEmployee().getEmployeeId());
        if (existingCompensation != null) {
            throw new RuntimeException("Compensation already exists for this employee");
        }

        compensation.setEmployee(employee);
        return compensationRepository.insert(compensation);
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for employee with id [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployee_EmployeeId(employeeId);

        if (compensation == null) {
            throw new RuntimeException("No compensation found for employeeId: " + employeeId);
        }

        return compensation;
    }
}
