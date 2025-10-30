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
@RequestMapping("/api/customer/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDto paymentDto) {
        PaymentResponse paymentResponse = paymentService.createPaymentUrl(paymentDto);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }

    @GetMapping("/return")
    public ResponseEntity<?> paymentReturn(@RequestParam Map<String,String> allParams) {
        PaymentResponse paymentResponse = paymentService.handlePaymentCallback(allParams);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }
}