package com.example.Ev.System.controller;

import com.example.Ev.System.dto.InvoiceDataDto;
import com.example.Ev.System.entity.Invoice;
import com.example.Ev.System.service.InvoiceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth/invoices")
public class InvoiceController {
    @Autowired
    private InvoiceServiceI invoiceServiceI;

    @PostMapping("/create/{appointmentId}")
    public ResponseEntity<Invoice> createInvoice(@PathVariable Integer appointmentId) {
        Invoice invoice = invoiceServiceI.createInvoice(appointmentId);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getRevenue(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        Double revenue = invoiceServiceI.getRevenue(
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate)
        );
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer id) {
        InvoiceDataDto data = invoiceServiceI.getInvoiceData(id);
        byte[] pdf = invoiceServiceI.generateInvoicePdf(data);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf")
                .body(pdf);
    }

    @PostMapping("/part/{appointmentId}")
    public ResponseEntity<Invoice> createPartInvoice(@PathVariable Integer appointmentId) {
        Invoice invoice = invoiceServiceI.createPartInvoice(appointmentId);
        return ResponseEntity.ok(invoice);
    }
}
