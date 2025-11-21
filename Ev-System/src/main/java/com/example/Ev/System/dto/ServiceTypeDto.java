package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTypeDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationEst;
}