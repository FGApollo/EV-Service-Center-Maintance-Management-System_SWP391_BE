package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSimpleDto {
    private Integer invoiceId;
    private String customerName;
    private BigDecimal totalAmount;
    private String ServiceName;
    private LocalDateTime createdAt;
}
