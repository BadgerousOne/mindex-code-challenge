
package com.mindex.challenge.data;

import java.time.LocalDate;

/**
 * The Compensation class represents a compensation record for an employee.
 * It includes details such as the associated employee, their salary, and the effective date of the compensation.
 */
public class Compensation {
    private Employee employee;
    private double salary;
    private LocalDate effectiveDate;

    /**
     * Default constructor for the Compensation class.
     * Initializes a new instance of the Compensation class with default values.
     */
    public Compensation() {
    }

    /**
     * Retrieves the employee associated with this compensation record.
     *
     * @return the employee object linked to this compensation record
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the employee associated with this instance of compensation.
     *
     * @param employee the employee to associate with this compensation
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * Retrieves the salary associated with this compensation record.
     *
     * @return the salary amount as a double
     */
    public double getSalary() {
        return salary;
    }

    /**
     * Sets the salary associated with this compensation record.
     *
     * @param salary the salary amount to be associated with this compensation
     */
    public void setSalary(double salary) {
        this.salary = salary;
    }

    /**
     * Retrieves the effective date associated with this compensation record.
     *
     * @return the effective date of the compensation
     */
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the effective date associated with this compensation record.
     *
     * @param effectiveDate the date from which this compensation becomes effective
     */
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
