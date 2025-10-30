package com.example.Ev.System.service;

import com.example.Ev.System.entity.Report;
import com.example.Ev.System.entity.User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public interface ReportServiceI {
    public Report generrateReport(String reportType, Instant start, Instant end, User generatedBy);
}
