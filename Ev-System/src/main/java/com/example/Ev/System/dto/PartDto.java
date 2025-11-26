package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartDto {
    private Integer PartId;
    private String PartName;
    private Double unitPrice;
    private Double importPrice;
    private String Description;
    private Integer minStockLevel;
    private Instant createdAt;
    private Integer Quantity;
}
