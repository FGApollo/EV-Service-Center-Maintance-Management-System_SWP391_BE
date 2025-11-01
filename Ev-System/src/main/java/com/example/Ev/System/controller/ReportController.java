package com.example.Ev.System.controller;

import com.example.Ev.System.service.PartUsageServiceI;
import com.example.Ev.System.service.ReportServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {
    @Autowired
    private PartUsageServiceI partUsageService;
    @Autowired
    private ReportServiceI reportService;

    @GetMapping("/trending-parts")
    public Object getTop5PartsUsedInLastMonth() {
        return partUsageService.getTop5PartsUsedInLastMonth();
    }

    @GetMapping("/trending-services/last-month")
    public ResponseEntity<List<Map.Entry<String, Long>>> getTrendingServicesLastMonth() {
        return ResponseEntity.ok(reportService.getTrendingServicesLastMonth());
    }

    @GetMapping("/trending-services/alltime")
    public ResponseEntity<List<Map.Entry<String, Long>>> getTrendingServices() {
        return ResponseEntity.ok(reportService.getTrendingServices());
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Double>> getRevenueReport() {
        return ResponseEntity.ok(reportService.getMonthlyRevenue());
    }

    @GetMapping("/profit")
    public ResponseEntity<Map<YearMonth, Double>> getProfitReport() {
        return ResponseEntity.ok(reportService.getMonthlyProfit());
    }
}
