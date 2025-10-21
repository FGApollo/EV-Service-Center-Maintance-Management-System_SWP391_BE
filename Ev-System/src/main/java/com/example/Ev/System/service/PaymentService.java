package com.example.Ev.System.service;

import com.example.Ev.System.entity.Invoice;
import com.example.Ev.System.entity.Payment;
import com.example.Ev.System.repository.InvoiceRepository;
import com.example.Ev.System.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PaymentService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InvoiceService invoiceService;

    public void processPayment(Integer invoiceId, String paymentMethod) {
        // Implement payment processing logic here
    }
    public Payment makePayment(Integer invoiceId, Payment paymentRequest) {
        paymentRequest.setInvoice(invoiceRepository.findById(invoiceId).orElse(null));
        paymentRequest.setPaymentDate(LocalDateTime.now());
        Payment payment = paymentRepository.save(paymentRequest);

        invoiceService.MarkInvoiceAsPaid(invoiceId);
        return payment;
    }
}
