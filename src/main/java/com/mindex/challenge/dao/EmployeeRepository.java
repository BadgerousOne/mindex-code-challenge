package com.mindex.challenge.dao;

import com.mindex.challenge.data.Employee;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Employee findFirstByEmployeeId(String employeeId);
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
    void deleteByEmployeeId(String employeeId);
}
