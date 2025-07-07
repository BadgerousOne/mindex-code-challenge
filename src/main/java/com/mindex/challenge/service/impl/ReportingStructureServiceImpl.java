package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        Employee employee = employeeService.read(employeeId);
        int numberOfReports = calculateNumberOfReports(employee, new HashSet<>());
        return new ReportingStructure(employee, numberOfReports);
    }

    /**
     * Calculates the total number of direct and indirect reports for a given employee.
     * This method performs a recursive traversal through the employee's reporting structure
     * to count all direct and indirect reports, ensuring no duplicate counts by utilizing a Set.
     *
     * @param employee the employee whose reporting structure is to be analyzed
     * @param counted a Set of employee IDs used to track which employees have already been counted
     * @return the total number of direct and indirect reports for the specified employee
     */
    private int calculateNumberOfReports(Employee employee, Set<String> counted) {
        if (employee.getDirectReports() == null) {
            return 0;
        }

        int total = 0;
        for (Employee report : employee.getDirectReports()) {
            // Get full employee object for each direct report
            Employee fullReport = employeeService.read(report.getEmployeeId());
            if (counted.add(fullReport.getEmployeeId())) {
                total++; // Count the direct report
                // Recursively count their reports
                total += calculateNumberOfReports(fullReport, counted);
            }
        }
        return total;
    }
}