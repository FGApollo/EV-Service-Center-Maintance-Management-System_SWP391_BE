package com.example.Ev.System.service;

import com.example.Ev.System.entity.Invoice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface InvoiceServiceI {
    public Invoice createInvoice(Integer appointmentId);
    public void MarkInvoiceAsPaid(Integer invoiceId);
    public double getRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
