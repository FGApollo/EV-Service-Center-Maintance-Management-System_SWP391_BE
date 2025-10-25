package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse{
    private Integer paymentId;
    private Integer invoiceId;
    private BigDecimal amount;
    private String method;
    private String message;
    private String paymentUrl;
    private String paymentType;
}
