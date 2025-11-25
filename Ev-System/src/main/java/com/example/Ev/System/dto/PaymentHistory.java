package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistory {
    private Integer paymentId;
    private Integer invoiceId;
    private BigDecimal amount;
    private String method;
}
