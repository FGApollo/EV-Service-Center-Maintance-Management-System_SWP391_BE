package com.example.Ev.System.controller;


import com.example.Ev.System.dto.PaymentDto;

import com.example.Ev.System.dto.PaymentResponse;

import com.example.Ev.System.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/customer/payments/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDto paymentDto) {
        PaymentResponse paymentResponse = paymentService.createPaymentUrl(paymentDto);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }

    @GetMapping("/api/auth/payments/return")
    public ResponseEntity<?> paymentReturn(@RequestParam Map<String,String> allParams) {

        PaymentResponse paymentResponse = paymentService.handlePaymentCallback(allParams);

        String redirectUrl;

        if ("00".equals(allParams.get("vnp_ResponseCode"))) {
//            redirectUrl = "http://localhost:5173/payment-return?status=success&amount="
//                    + paymentResponse.getAmount();
            redirectUrl = "https://ev-vercel.vercel.app/payment-return?status=success&amount="
                    + paymentResponse.getAmount();
        } else {
            redirectUrl = "http://localhost:5173/payment-return?status=failed";
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();
    }

}