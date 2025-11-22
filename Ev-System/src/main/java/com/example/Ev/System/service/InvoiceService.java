package com.example.Ev.System.service;

import com.example.Ev.System.dto.InvoiceDetailDto;
import com.example.Ev.System.dto.InvoiceSimpleDto;
import com.example.Ev.System.entity.AppointmentService;
import com.example.Ev.System.entity.Invoice;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.exception.NotFoundException;
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
public class InvoiceService implements InvoiceServiceI {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private AppointmentServiceRepository appointmentServiceRepository;

    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    public Invoice createInvoice(Integer appointmentId) {
        if (invoiceRepository.existsByAppointment_Id(appointmentId)) {
            throw new RuntimeException("Invoice already exists for this appointment");
        }

        List<AppointmentService> service = appointmentServiceRepository.findByAppointmentId(appointmentId);
        if (service.isEmpty()) {
            throw new NotFoundException("Service not found for this appointment");
        }
        BigDecimal totalAmount = service.stream()
                .map(s -> s.getServiceType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Service not found for this appointment");
        }

        ServiceAppointment appointment = serviceAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

//        BigDecimal currentServicePrice = service.stream()
//                .map(s -> s.getServiceType().getPrice())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String serviceName = service.get(0).getServiceType().getName();

        Invoice invoice = new Invoice();
        invoice.setAppointment(appointment);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("unpaid");
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setServiceName(serviceName);
        return invoiceRepository.save(invoice);
    }

    public void MarkInvoiceAsPaid(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus("PAID");
        invoice.setPaymentDate(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }

    public double getRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findByStatusAndPaymentDateBetween("PAID", startDate, endDate);
        return invoices.stream()
                .map(Invoice::getTotalAmount)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    @Override
    public List<InvoiceSimpleDto> getInvoices(Integer centerId) {
        return invoiceRepository.findInvoicesByStatusAndCenter("PAID", centerId).stream()
                .map(invoice -> new InvoiceSimpleDto(
                        invoice.getId(),
                        invoice.getTotalAmount(),
                        invoice.getServiceName(),
                        invoice.getCreatedAt()

                ))
                .toList();
    }

    @Override
    public InvoiceDetailDto getInvoice(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        var customer = invoice.getAppointment().getCustomer();
        var vehicle = invoice.getAppointment().getVehicle();
        var center = invoice.getAppointment().getServiceCenter();
        return new InvoiceDetailDto(
                invoice.getId(),
                customer.getFullName(),
                customer.getPhone(),
                customer.getEmail(),
                vehicle.getVin(),
                vehicle.getModel(),
                center.getId(),
                invoice.getTotalAmount(),
                invoice.getServiceName(),
                invoice.getCreatedAt(),
                invoice.getPaymentDate()
        );
    }
}
