package com.example.Ev.System.service;

import com.example.Ev.System.dto.InvoiceDataDto;
import com.example.Ev.System.dto.InvoiceDetailDto;
import com.example.Ev.System.dto.InvoiceSimpleDto;
import com.example.Ev.System.entity.Invoice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface InvoiceServiceI {
    public Invoice createInvoice(Integer appointmentId);
    public void MarkInvoiceAsPaid(Integer invoiceId);
    public double getRevenue(LocalDateTime startDate, LocalDateTime endDate);
    List<InvoiceSimpleDto> getInvoices(Integer centerId);
    InvoiceDetailDto getInvoice(Integer invoiceId);
    InvoiceDataDto getInvoiceData(Integer appointmentId);
    byte[] generateInvoicePdf(InvoiceDataDto invoiceDataDto);
}
