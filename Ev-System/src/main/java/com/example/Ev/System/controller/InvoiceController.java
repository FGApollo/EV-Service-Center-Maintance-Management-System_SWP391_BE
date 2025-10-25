package com.example.Ev.System.controller;

import com.example.Ev.System.entity.Invoice;
import com.example.Ev.System.service.InvoiceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/invoices")
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
}
