package com.example.Ev.System.service;

import com.example.Ev.System.entity.AppointmentService;
import com.example.Ev.System.entity.Invoice;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.repository.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private AppointmentServiceRepository appointmentServiceRepository;
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;
    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    public Invoice createInvoice(Integer appointmentId) {
        List<AppointmentService> service = appointmentServiceRepository.findByAppointmentId(appointmentId);

        BigDecimal totalAmount = service.stream()
                .map(s -> s.getServiceType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice invoice = new Invoice();
        ServiceAppointment appointment = serviceAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found "));

        invoice.setAppointment(appointment);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("UNPAID");
        return invoiceRepository.save(invoice);
    }

    public Invoice MarkInvoiceAsPaid(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus("PAID");
        invoice.setPaymentDate(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public double getRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findByStatusAndPaymentDateBetween("PAID", startDate, endDate);
        return invoices.stream()
                .map(Invoice::getTotalAmount)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }
}
