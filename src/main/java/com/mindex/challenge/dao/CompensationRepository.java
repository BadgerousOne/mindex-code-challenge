package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Compensation entities in the MongoDB database.
 * Provides methods for CRUD operations and custom query execution for Compensation data.
 */
@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {
    Compensation findByEmployee_EmployeeId(String employeeId);
}
