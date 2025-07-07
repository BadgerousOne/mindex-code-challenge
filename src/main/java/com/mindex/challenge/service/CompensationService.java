package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * The CompensationService interface provides methods for managing and retrieving compensation
 * details associated with employees in the system. It allows for creating new compensation records
 * and fetching existing compensation details for a specific employee based on their employeeId.
 */
public interface CompensationService {
    Compensation create(Compensation compensation);
    Compensation read(String employeeId);
}
