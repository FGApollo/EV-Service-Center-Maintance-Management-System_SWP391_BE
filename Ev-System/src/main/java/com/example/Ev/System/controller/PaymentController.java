package com.example.Ev.System.controller;


import com.example.Ev.System.dto.PaymentDto;

import com.example.Ev.System.dto.PaymentResponse;

import com.example.Ev.System.dto.RefundRequestDto;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.service.PaymentService;
import com.example.Ev.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserService userService;

    @PostMapping("/api/customer/payments/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDto paymentDto) {
        PaymentResponse paymentResponse = paymentService.createPaymentUrl(paymentDto);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }

//    @GetMapping("/api/auth/payments/return")
//    public ResponseEntity<?> paymentReturn(@RequestParam Map<String,String> allParams) {
//
//        PaymentResponse paymentResponse = paymentService.handlePaymentCallback(allParams);
//
//        String redirectUrl;
//
////        if ("00".equals(allParams.get("vnp_ResponseCode"))) {
//////            redirectUrl = "http://localhost:5173/payment-return?status=success&amount="
//////                    + paymentResponse.getAmount();
////            redirectUrl = "https://ev-vercel.vercel.app/payment-return";
////
////        } else {
////            redirectUrl = "http://localhost:5173/payment-return?status=failed";
////        }
//        String responseCode = allParams.getOrDefault("vnp_ResponseCode",
//                allParams.get("vnp_TxnResponseCode"));
//
//        if ("00".equals(responseCode)) {
//            redirectUrl = "https://ev-vercel.vercel.app/payment-return?status=success";
//        } else {
//            redirectUrl = "https://ev-vercel.vercel.app/payment-return?status=failed";
//        }
//
//
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .header("Location", redirectUrl)
//                .build();
//    }

        @GetMapping("/api/auth/payments/return")
        public ResponseEntity<?> paymentReturn(@RequestParam Map<String,String> allParams) {

            PaymentResponse paymentResponse = paymentService.handlePaymentCallback(allParams);

            String redirectUrl;

            if ("00".equals(allParams.get("vnp_ResponseCode"))) {
                redirectUrl = "http://localhost:5173/payment-return?status=success&amount="
                        + paymentResponse.getAmount();
            } else {
                redirectUrl = "http://localhost:5173/payment-return?status=failed";
            }

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }

    @GetMapping("/api/customer/payments/history")
    public ResponseEntity<?> history(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        return ResponseEntity.ok(paymentService.getPaymentHistoryByUser(currentUser.getId()));
    }

    @PostMapping("/api/refunds")
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    public ResponseEntity<?> createRefund(@RequestBody RefundRequestDto refundRequestDto) {
        PaymentResponse paymentResponse = paymentService.createRefundUrl(refundRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }

    @GetMapping("/api/auth/refund/return")
    public ResponseEntity<?> refundReturn(@RequestParam Map<String,String> allParams) {

        paymentService.handleRefundCallback(allParams);

        String redirectUrl;

        if ("00".equals(allParams.get("vnp_ResponseCode"))) {
            redirectUrl = "http://localhost:5173/refund-return?status=success";
        } else {
            redirectUrl = "http://localhost:5173/refund-return?status=failed";
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();
    }

    @PostMapping("/api/cash-payment/{invoiceId}")
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    public ResponseEntity<?> createCashPayment(@PathVariable Integer invoiceId ) {
        return ResponseEntity.ok(paymentService.createCashPayment(invoiceId));
    }

}