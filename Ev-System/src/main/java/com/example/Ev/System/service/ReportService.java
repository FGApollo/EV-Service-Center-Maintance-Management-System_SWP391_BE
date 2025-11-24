package com.example.Ev.System.service;

import com.example.Ev.System.dto.CenterStats;
import com.example.Ev.System.dto.PartStockReport;
import com.example.Ev.System.dto.PaymentMethodStats;
import com.example.Ev.System.dto.RevenueResponse;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService implements ReportServiceI {
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PartUsageRepository partUsageRepository;
    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;
    @Autowired
    PaymentRepository paymentRepository;

    //REVENUE REPORT
    @Override
    public Map<String, Double> getMonthlyRevenue() {
        List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");

        return paidInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getPaymentDate().getMonth().toString() + " " + invoice.getPaymentDate().getYear(),
                        Collectors.summingDouble(invoice -> invoice.getTotalAmount().doubleValue())
                ));
    }

    //PROFIT REPORT
    @Override
    public Map<YearMonth, Double> getMonthlyProfit() {

        // Calculate revenue from paid invoices
        Map<YearMonth, Double> revenueByMonth = invoiceRepository.findByStatus("PAID").stream()
                .collect(Collectors.groupingBy(
                        invoice -> YearMonth.from(invoice.getPaymentDate()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()),
                        Collectors.summingDouble(invoice -> invoice.getTotalAmount().doubleValue())
                ));

        // Calculate costs from part usage
        Map<YearMonth, Double> costByMonth = partUsageRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        partUsage -> YearMonth.from(partUsage.getRecord().getEndTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()),
                        Collectors.summingDouble(partUsage -> partUsage.getUnitCost() * partUsage.getQuantityUsed())
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

    //TRENDING SERVICES
    @Override
    @Transactional(readOnly = true)
    public List<Map.Entry<String, Long>> getTrendingServicesLastMonth() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        Instant startOfMonth = firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<ServiceAppointment> recentAppointments = serviceAppointmentRepository.findByCreatedAtAfter(startOfMonth);

        return calculateTrendingServices(recentAppointments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map.Entry<String, Long>> getTrendingServices() {
        List<ServiceAppointment> appointments = serviceAppointmentRepository.findAll();
        return calculateTrendingServices(appointments);
    }

    @Override
    public RevenueResponse getCurrentMonthRevenue() {
        YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());
        YearMonth lastMonth = currentMonth.minusMonths(1);

        List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");
        Map<YearMonth, Double> revenueByMonth = paidInvoices.stream()
                .collect(Collectors.groupingBy(
                   invoice -> YearMonth.from(invoice.getPaymentDate()
                           .atZone(ZoneId.systemDefault())
                           .toLocalDate()),
                        Collectors.summingDouble(invoice -> invoice.getTotalAmount().doubleValue())
                ));

        double thisMonthRevenue = revenueByMonth.getOrDefault(currentMonth, 0.0);
        double lastMonthRevenue = revenueByMonth.getOrDefault(lastMonth, 0.0);

        int percentChange = 0;
        if (lastMonthRevenue >0) {
            percentChange = (int) Math.round(((thisMonthRevenue-lastMonthRevenue)/lastMonthRevenue)*100);
        }

        String trend;
        if(lastMonthRevenue < thisMonthRevenue) {
            trend = "UP";
        } else {
            trend = "DOWN";
        }

        return new RevenueResponse(
                Math.round(thisMonthRevenue),
                Math.round(lastMonthRevenue),
                percentChange,
                trend
        );
    }

    @Override
    public Double getCurrentMonthExpense() {
        YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());
        List<PartUsage> allPartUsage = partUsageRepository.findAll();

        return allPartUsage.stream()
                .filter(partUsage -> {
                    if(partUsage.getRecord() == null || partUsage.getRecord().getEndTime() == null) {return false;}
                    YearMonth usageMonth = YearMonth.from(
                            partUsage.getRecord().getEndTime()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                    );
                    return usageMonth.equals(currentMonth);
                })
                .mapToDouble(usage ->
                        usage.getUnitCost()*usage.getQuantityUsed()
                ).sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getRevenueByService() {
        List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");

        List<ServiceAppointment> appointments = paidInvoices.stream()
                .map(Invoice::getAppointment)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Double> revenueByService = appointments.stream()
                .flatMap(appointment -> appointment.getServiceTypes().stream())
                .collect(Collectors.groupingBy(
                        ServiceType::getName,
                        Collectors.summingDouble(serviceType ->
                                serviceType.getPrice() != null ? serviceType.getPrice().doubleValue() : 0.0)
                ));

        return revenueByService;
    }

    @Transactional
    @Override
    public Map<String, PaymentMethodStats> getRevenueByPaymentMethod() {
        List<Payment> allPayments = paymentRepository.findAll();

        List<Payment> validPayments = allPayments.stream()
                .filter(p -> p.getInvoice() != null &&
                        "PAID".equalsIgnoreCase(p.getInvoice().getStatus()))
                .toList();

        // Keep only ONE payment per invoice (latest)
        Map<Integer, Payment> latestPaymentPerInvoice =
                validPayments.stream()
                        .collect(Collectors.toMap(
                                p -> p.getInvoice().getId(),
                                p -> p,
                                (p1, p2) -> p1.getPaymentDate()
                                        .isAfter(p2.getPaymentDate()) ? p1 : p2
                        ));

        List<Payment> filteredPayments = new ArrayList<>(latestPaymentPerInvoice.values());

        Map<String, List<Payment>> grouped = filteredPayments.stream()
                .collect(Collectors.groupingBy(
                        payment -> Optional.ofNullable(payment.getMethod()).orElse("UNKNOWN")
                ));

        double totalAmount = validPayments.stream()
                .mapToDouble(payment -> payment.getAmount().doubleValue())
                .sum();

        Map<String, PaymentMethodStats> stats = new HashMap<>();
        for (Map.Entry<String, List<Payment>> entry : grouped.entrySet()) {
            String method = entry.getKey();
            List<Payment> payments = entry.getValue();

            long count = payments.size();
            double amount = payments.stream()
                    .mapToDouble(p -> p.getAmount().doubleValue())
                    .sum();
            double percentage = totalAmount > 0 ? (amount / totalAmount) *100 : 0;
            stats.put(method, new PaymentMethodStats(count, amount, Math.round(percentage)));
        }
        return stats;
    }

    @Override
    @Transactional
    public List<PartStockReport> getPartStockReport() {
        List<Part> parts = partRepository.findAll();

        return parts.stream().map(part -> {
            int totalStock = part.getInventories().stream()
                    .mapToInt(inventories -> inventories.getQuantity() != null ? inventories.getQuantity() : 0)
                    .sum();
            int totalUsage = part.getPartUsages().stream()
                    .mapToInt(partUsage -> partUsage.getQuantityUsed() != null ? partUsage.getQuantityUsed() : 0)
                    .sum();

            return new PartStockReport(
                    part.getId(),
                    part.getName(),
                    part.getMinStockLevel() != null ? part.getMinStockLevel() : 0,
                    totalStock,
                    totalUsage
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Map<Integer, CenterStats> getRevenueByCenter(Integer id) {
        YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());
        YearMonth lastMonth = currentMonth.minusMonths(1);

        List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");
        if (paidInvoices.isEmpty()) {
            throw new NotFoundException("Not Found Paid Invoice ");
        }
        Map<Optional<Integer>, List<Invoice>> groupedCenter = paidInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> Optional.ofNullable(invoice.getAppointment().getServiceCenter().getId())
                ));


        Map<Integer, CenterStats> stats = new HashMap<>();
        for (Map.Entry<Optional<Integer>, List<Invoice>> entry : groupedCenter.entrySet()) {
            Integer centerId = entry.getKey().get();
            List<Invoice> invoices = entry.getValue();

            Map<YearMonth, Double> revenueByMonth = invoices.stream()
                    .collect(Collectors.groupingBy(
                            invoice -> YearMonth.from(invoice.getPaymentDate()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()),
                            Collectors.summingDouble(invoice -> invoice.getTotalAmount().doubleValue())
                    ));

            double thisMonthRevenue = revenueByMonth.getOrDefault(currentMonth, 0.0);
            double lastMonthRevenue = revenueByMonth.getOrDefault(lastMonth, 0.0);

            int percentChange = 0;
            if (lastMonthRevenue >0) {
                percentChange = (int) Math.round(((thisMonthRevenue-lastMonthRevenue)/lastMonthRevenue)*100);
            }

            String trend;
            if(lastMonthRevenue < thisMonthRevenue) {
                trend = "UP";
            } else {
                trend = "DOWN";
            }
            stats.put(centerId, new CenterStats(centerId, thisMonthRevenue, lastMonthRevenue, percentChange, trend));
        }

        if (id != null && stats.containsKey(id)) {
            return Map.of(id, stats.get(id));
        } else {
            return stats;
        }
    }

//    @Override
//    @Transactional
//    public Map<String, Double> getRevenueByServiceCurrentMonth() {
//        YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());
//
//        // Get all paid invoices
//        List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");
//
//        // Filter only invoices whose payment date is in the current month
//        List<Invoice> currentMonthInvoices = paidInvoices.stream()
//                .filter(invoice -> {
//                    YearMonth invoiceMonth = YearMonth.from(
//                            invoice.getPaymentDate()
//                                    .atZone(ZoneId.systemDefault())
//                                    .toLocalDate()
//                    );
//                    return invoiceMonth.equals(currentMonth);
//                })
//                .toList();
//
//        // Map invoices → appointments → services → sum prices
//        List<ServiceAppointment> appointments = currentMonthInvoices.stream()
//                .map(Invoice::getAppointment)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        return appointments.stream()
//                .flatMap(appointment -> appointment.getServiceTypes().stream())
//                .collect(Collectors.groupingBy(
//                        ServiceType::getName,
//                        Collectors.summingDouble(serviceType ->
//                                serviceType.getPrice() != null ? serviceType.getPrice().doubleValue() : 0.0)
//                ));
//    }
@Override
@Transactional
public Map<String, Double> getRevenueByServiceCurrentMonth() {
    YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());

    // Get all paid invoices
    List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");

    // Filter only invoices whose payment date is in the current month
    List<Invoice> currentMonthInvoices = paidInvoices.stream()
            .filter(invoice -> {
                YearMonth invoiceMonth = YearMonth.from(
                        invoice.getPaymentDate()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                );
                return invoiceMonth.equals(currentMonth);
            })
            .toList();

    // Map invoices → appointments → services → sum prices
//    List<ServiceAppointment> appointments = currentMonthInvoices.stream()
//            .map(Invoice::getAppointment)
//            .filter(Objects::nonNull)
//            .collect(Collectors.toList());

    return currentMonthInvoices.stream()
            .collect(Collectors.groupingBy(
                    Invoice::getServiceName,
                    Collectors.summingDouble(invoice ->
                            invoice.getTotalAmount() != null ? invoice.getTotalAmount().doubleValue() : 0.0)
            ));
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
