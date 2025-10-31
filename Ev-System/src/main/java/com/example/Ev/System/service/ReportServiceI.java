package com.example.Ev.System.service;

import com.example.Ev.System.entity.Report;
import com.example.Ev.System.entity.ServiceType;
import com.example.Ev.System.entity.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
public interface ReportServiceI {
    public Map<String, Double> getMonthlyRevenue();
    public Map<YearMonth, Double> getMonthlyProfit();
    public List<Map.Entry<String, Long>> getTrendingServicesLastMonth();
    public List<Map.Entry<String, Long>> getTrendingServices();
}
