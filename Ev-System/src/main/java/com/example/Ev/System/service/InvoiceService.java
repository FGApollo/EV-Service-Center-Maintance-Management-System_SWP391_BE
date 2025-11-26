package com.example.Ev.System.service;

import com.example.Ev.System.dto.CustomerInvoiceDto;
import com.example.Ev.System.dto.InvoiceDataDto;
import com.example.Ev.System.dto.InvoiceDetailDto;
import com.example.Ev.System.dto.InvoiceSimpleDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.entity.AppointmentService;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private PartUsageRepository partUsageRepository;

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
            throw new BadRequestException("Total amount must be greater than zero");
        }

        ServiceAppointment appointment = serviceAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

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
                        invoice.getAppointment().getCustomer().getFullName(),
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

    @Override
    @Transactional(readOnly = true)
    public InvoiceDataDto getInvoiceData(Integer appointmentId) {
        ServiceAppointment app = serviceAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        List<ServiceType> services = new ArrayList<>(app.getServiceTypes());

        List<PartUsage> partUsages = app.getMaintenanceRecords().stream()
                .flatMap(maintenanceRecord -> maintenanceRecord.getPartUsages().stream())
                .toList();

        return new InvoiceDataDto(app, services, partUsages);
    }

    @Override
    public byte[] generateInvoicePdf(InvoiceDataDto invoiceDataDto) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfFont unicode = PdfFontFactory.createFont("font/Roboto-Regular.ttf", PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);


            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);


            document.add(new Paragraph("Invoice").setFont(unicode).setFontSize(16));
            document.add(new Paragraph("Appointment Id: " + invoiceDataDto.getAppointment().getId())
                    .setFont(unicode));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Customer Name: "
                    + invoiceDataDto.getAppointment().getCustomer().getFullName()).setFont(unicode));
            document.add(new Paragraph("Vehicle: "
                    + invoiceDataDto.getAppointment().getVehicle().getModel()).setFont(unicode));

            //service
            Table serviceTable = new Table(2);
            serviceTable.addCell(new Cell().add(new Paragraph("Service Name").setFont(unicode)));
            serviceTable.addCell(new Cell().add(new Paragraph("Price").setFont(unicode)));

            invoiceDataDto.getServices().forEach(service -> {
                serviceTable.addCell(new Cell().add(new Paragraph(service.getName()).setFont(unicode)));
                serviceTable.addCell(new Cell().add(new Paragraph(service.getPrice().toString()).setFont(unicode)));
            });

            document.add(new Paragraph("Services Used:").setFont(unicode));
            document.add(serviceTable);
            document.add(new Paragraph("\n"));

            //parts
            Table partTable = new Table(4);
            partTable.addCell(new Cell().add(new Paragraph("Part Name").setFont(unicode)));
            partTable.addCell(new Cell().add(new Paragraph("Quantity").setFont(unicode)));
            partTable.addCell(new Cell().add(new Paragraph("Unit Cost").setFont(unicode)));
            partTable.addCell(new Cell().add(new Paragraph("Total").setFont(unicode)));

            invoiceDataDto.getParts().forEach(part -> {
                double totalCost = part.getUnitCost() * part.getQuantityUsed();

                partTable.addCell(new Cell().add(new Paragraph(part.getPart().getName()).setFont(unicode)));
                partTable.addCell(new Cell().add(new Paragraph(String.valueOf(part.getQuantityUsed())).setFont(unicode)));
                partTable.addCell(new Cell().add(new Paragraph(String.valueOf(part.getUnitCost())).setFont(unicode)));
                partTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalCost)).setFont(unicode)));
            });

            document.add(new Paragraph("Parts Used:").setFont(unicode));
            document.add(partTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    @Override
    @Transactional
    public Invoice createPartInvoice(Integer appointmentId) {
        ServiceAppointment appointment = serviceAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        MaintenanceRecord record = appointment.getMaintenanceRecords().stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new RuntimeException("No maintenance record found for this appointment"));

        BigDecimal totalAmount = partUsageRepository.findByRecord(record).stream()
                .map(partUsage ->
                        BigDecimal.valueOf(partUsage.getUnitCost() * partUsage.getQuantityUsed()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice invoice = new Invoice();
        invoice.setAppointment(appointment);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("unpaid");
        invoice.setCreatedAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    @Override
    public CustomerInvoiceDto getCustomerInvoice(InvoiceDataDto invoiceDataDto) {
        return new CustomerInvoiceDto(
                invoiceDataDto.getAppointment().getId(),
                invoiceDataDto.getAppointment().getCustomer().getFullName(),
                invoiceDataDto.getAppointment().getVehicle().getModel(),
                invoiceDataDto.getServices(),
                invoiceDataDto.getParts()
        );
    }

}
