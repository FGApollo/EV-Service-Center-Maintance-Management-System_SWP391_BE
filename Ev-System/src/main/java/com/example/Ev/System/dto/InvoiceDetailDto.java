package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailDto {
    private Integer invoiceId;

    // Customer info
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Vehicle info
    private String vehicleVin;
    private String vehicleModel;
    private Integer centerId;

    // Invoice info
    private BigDecimal totalAmount;
    private String serviceName;
    private LocalDateTime createdAt;
    private LocalDateTime paymentDate;

}
