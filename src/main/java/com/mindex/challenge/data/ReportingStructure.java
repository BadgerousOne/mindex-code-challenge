package com.mindex.challenge.data;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    /**
     * Default constructor for the ReportingStructure class.
     * This initializes a new instance of ReportingStructure with default values.
     */
    public ReportingStructure() {

    }

    /**
     * Creates an instance of ReportingStructure with the specified employee and number of reports.
     *
     * @param employee the employee associated with the reporting structure
     * @param numberOfReports the total number of direct and indirect reports for the specified employee
     */
    public ReportingStructure(Employee employee, int numberOfReports) {
        this.employee = employee;
        this.numberOfReports = numberOfReports;
    }

    /**
     * Retrieves the employee associated with the reporting structure.
     *
     * @return the employee associated with this reporting structure
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the employee associated with the reporting structure.
     *
     * @param employee the employee to associate with this reporting structure
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * Retrieves the total number of reports associated with the reporting structure.
     *
     * @return the number of direct and indirect reports for the specified employee
     */
    public int getNumberOfReports() {
        return numberOfReports;
    }

    /**
     * Sets the total number of reports associated with the reporting structure.
     *
     * @param numberOfReports the number of direct and indirect reports for the specified employee
     */
    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}
