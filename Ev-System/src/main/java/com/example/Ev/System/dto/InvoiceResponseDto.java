package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDto {
    private Integer invoiceId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime paymentDate;
    private String serviceName;
    private LocalDateTime createdAt;
}

