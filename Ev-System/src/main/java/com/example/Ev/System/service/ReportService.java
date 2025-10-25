package com.example.Ev.System.service;

import com.example.Ev.System.entity.Report;
import com.example.Ev.System.entity.ServiceType;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReportService implements ReportServiceI {
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Override
    public Report generrateReport(String reportType, Instant start, Instant end, User generatedBy) {
        ServiceType serviceTypes = serviceTypeRepository.findTopByOrderByDurationEst();
        return null;
    }
}
