package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueResponse {
    private Long thisMonth;
    private Long lastMonth;
    private int percentChange;
    private String trend;
}
