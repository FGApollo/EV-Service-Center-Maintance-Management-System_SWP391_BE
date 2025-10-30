package com.example.Ev.System.service;

import com.example.Ev.System.dto.PaymentDto;
import com.example.Ev.System.dto.PaymentResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface PaymentServiceI {
    public PaymentResponse createPaymentUrl(PaymentDto paymentDto);
    public PaymentResponse handlePaymentCallback(Map<String, String> allParams);
}
