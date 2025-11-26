package com.example.Ev.System.service;

import com.example.Ev.System.dto.PaymentDto;
import com.example.Ev.System.dto.PaymentHistory;
import com.example.Ev.System.dto.PaymentResponse;
import com.example.Ev.System.dto.RefundRequestDto;
import com.example.Ev.System.entity.Payment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PaymentServiceI {
    public PaymentResponse createPaymentUrl(PaymentDto paymentDto);
    public PaymentResponse handlePaymentCallback(Map<String, String> allParams);
    List<PaymentHistory> getPaymentHistoryByUser(Integer customerId);
    PaymentResponse createRefundUrl(RefundRequestDto dto);
    void handleRefundCallback(Map<String, String> allParams);
    Payment createCashPayment(Integer invoiceId);
    PaymentResponse createPartPaymentUrl(Integer appointmentId);
}
