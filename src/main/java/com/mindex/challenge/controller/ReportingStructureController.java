package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReportingStructureController  {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureController.class);

    @Autowired
    private ReportingStructureService reportingStructureService;

    @GetMapping("/reporting-structure/{employeeId}")
    public ReportingStructure getReportingStructure(@PathVariable String employeeId) {
        LOG.debug("Received reporting structure get request for employeeId [{}]", employeeId);

        try {
            ReportingStructure result = reportingStructureService.getReportingStructure(employeeId);
            LOG.debug("Reporting structure for [{}]", result);
            return result;
        } catch (Exception e) {
            LOG.error("Failed to get reporting structure for employeeId [{}]", employeeId, e);
            throw e;
        }
    }
}
