package com.example.Ev.System.service;

import com.example.Ev.System.entity.*;
import com.example.Ev.System.repository.InvoiceRepository;
import com.example.Ev.System.repository.PartUsageRepository;
import com.example.Ev.System.repository.ServiceAppointmentRepository;
import com.example.Ev.System.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService implements ReportServiceI {
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PartUsageRepository partUsageRepository;
    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    @Override
    public Map<String, Double> getMonthlyRevenue() {
        List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");

        return paidInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getPaymentDate().getMonth().toString() + " " + invoice.getPaymentDate().getYear(),
                        Collectors.summingDouble(invoice -> invoice.getTotalAmount().doubleValue())
                ));
    }

    @Override
    public Map<YearMonth, Double> getMonthlyProfit() {

        // Calculate revenue from paid invoices
        Map<YearMonth, Double> revenueByMonth = invoiceRepository.findByStatus("PAID").stream()
                .collect(Collectors.groupingBy(
                        invoice -> YearMonth.from(invoice.getPaymentDate()),
                        Collectors.summingDouble(invoice -> invoice.getTotalAmount().doubleValue())
                ));

        // Calculate costs from part usage
        Map<YearMonth, Double> costByMonth = partUsageRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        partUsage -> YearMonth.from(partUsage.getRecord().getEndTime()),
                        Collectors.summingDouble(partUsage -> partUsage.getPart().getUnitPrice().doubleValue() * partUsage.getQuantityUsed())
                ));

        Set<YearMonth> allMonths = new HashSet<>();
        allMonths.addAll(revenueByMonth.keySet());
        allMonths.addAll(costByMonth.keySet());

        // Calculate profit by month
        Map<YearMonth, Double> profitByMonth = new TreeMap<>();
        for (YearMonth month : allMonths) {
            double monthRevenue = revenueByMonth.getOrDefault(month, 0.0);
            double monthCost = costByMonth.getOrDefault(month, 0.0);
            profitByMonth.put(month, monthRevenue - monthCost);
        }

        return profitByMonth;
    }

    @Override
    public List<Map.Entry<String, Long>> getTrendingServicesLastMonth() {
        Instant oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        List<ServiceAppointment> recentAppointments = serviceAppointmentRepository.findByCreatedAtAfter(oneMonthAgo);

        return calculateTrendingServices(recentAppointments);
    }

    public List<Map.Entry<String, Long>> getTrendingServices() {
        List<ServiceAppointment> appointments = serviceAppointmentRepository.findAll();
        return calculateTrendingServices(appointments);
    }

    // Common logic for counting + sorting
    private List<Map.Entry<String, Long>> calculateTrendingServices(List<ServiceAppointment> appointments) {
        Map<String, Long> serviceCount = appointments.stream()
                .flatMap(appointment -> appointment.getServiceTypes().stream())
                .map(as -> as.getName())
                .collect(Collectors.groupingBy(
                        serviceName -> serviceName,
                        Collectors.counting()
                ));

        return serviceCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

}
