package com.example.Ev.System.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleDto {
    private Integer id;
    private String vin;
    private String model;
    private Integer year;
    private String color;
    private String licensePlate;

    // BigDecimal là kiểu thập phân giống double, float nhưng đảm bảo độ chính xác cao và không bị sai số do biểu diễn nhị phân.
}
