package com.example.Ev.System.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleDto {
    private Integer id;

    @NotBlank(message = "Vin không được để trống")
    @Size(max = 50, message = "Vin tối đa 50 ký tự")
    private String vin;

    @NotBlank(message = "Model không được để trống")
    @Size(max = 50, message = "Model tối đa 50 ký tự")
    private String model;

    private Integer year;

    @Size(max = 30, message = "Màu tối đa 30 ký tự")
    private String color;

    @NotBlank(message = "Biển số không được để trống")
    @Size(min = 7, max = 15, message = "Biển số phải từ 7-15 ký tự")
    @Pattern(regexp = "^[A-Z0-9\\- ]+$", message = "Biển số chỉ gồm chữ in hoa, số, khoảng trắng hoặc dấu gạch nối")
    private String licensePlate;

    // BigDecimal là kiểu thập phân giống double, float nhưng đảm bảo độ chính xác cao và không bị sai số do biểu diễn nhị phân.
}
