package com.example.Ev.System.service;

import com.example.Ev.System.dto.CenterStats;
import com.example.Ev.System.dto.PartStockReport;
import com.example.Ev.System.dto.PaymentMethodStats;
import com.example.Ev.System.dto.RevenueResponse;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
public interface ReportServiceI {
    public Map<String, Double> getMonthlyRevenue();
    public Map<YearMonth, Double> getMonthlyProfit();
    public List<Map.Entry<String, Long>> getTrendingServicesLastMonth();
    public List<Map.Entry<String, Long>> getTrendingServices();
    public RevenueResponse getCurrentMonthRevenue();
    public Double getCurrentMonthExpense();
    public Map<String, Double> getRevenueByService();
    Map<String, PaymentMethodStats> getRevenueByPaymentMethod();
    public List<PartStockReport> getPartStockReport();
    Map<Integer, CenterStats> getRevenueByCenter( Integer id);
}
