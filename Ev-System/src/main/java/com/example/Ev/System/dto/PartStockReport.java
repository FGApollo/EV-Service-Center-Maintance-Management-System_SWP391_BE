package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartStockReport {
    private Integer id;
    private String name;
    private int minStockLevel;
    private int totalStock;
    private int totalUsage;
}
