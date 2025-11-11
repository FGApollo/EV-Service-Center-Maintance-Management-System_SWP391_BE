package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterStats {
    private Integer centerId;
    private Double thisMonth;
    private Double lastMonth;
    private int percentChange;
    private String trend;
}
