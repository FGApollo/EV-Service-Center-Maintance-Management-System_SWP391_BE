package com.example.Ev.System.controller;

import com.example.Ev.System.dto.CenterStats;
import com.example.Ev.System.dto.PartStockReport;
import com.example.Ev.System.dto.PaymentMethodStats;
import com.example.Ev.System.dto.RevenueResponse;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.service.PartUsageServiceI;
import com.example.Ev.System.service.ReportServiceI;
import com.example.Ev.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/management/reports")
public class ReportController {
    @Autowired
    private PartUsageServiceI partUsageService;
    @Autowired
    private ReportServiceI reportService;
    @Autowired
    private UserService userService;

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

    @GetMapping("/revenue/current-month")
    public ResponseEntity<RevenueResponse> getRevenueCurrentMonth() {
        RevenueResponse response = reportService.getCurrentMonthRevenue();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expense/current-month")
    public ResponseEntity<Double> getCurrentMonthExpense() {
        Double expense = reportService.getCurrentMonthExpense();
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/revenue/service")
    public ResponseEntity<Map<String, Double>> getRevenueByService() {
        Map<String, Double> revenueByService = reportService.getRevenueByService();
        return ResponseEntity.ok(revenueByService);
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<Map<String, PaymentMethodStats>> getPaymentMethods() {
        Map<String, PaymentMethodStats> paymentMethods = reportService.getRevenueByPaymentMethod();
        return ResponseEntity.ok(paymentMethods);
    }

    @GetMapping("/parts/stock-report")
    public ResponseEntity<List<PartStockReport>> getPartStockReport() {
        List<PartStockReport> report = reportService.getPartStockReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/center")
    public ResponseEntity<Map<Integer, CenterStats>> getRevenueByCenter(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();
        Map<Integer, CenterStats> revenueByCenter = reportService.getRevenueByCenter(centerId);
        return ResponseEntity.ok(revenueByCenter);
    }

    @GetMapping("/revenue/service/current-month")
    public ResponseEntity<Map<String, Double>> getRevenueByServiceCurrentMonth() {
        Map<String, Double> revenueByServiceCurrentMonth = reportService.getRevenueByServiceCurrentMonth();
        return ResponseEntity.ok(revenueByServiceCurrentMonth);
    }

}
