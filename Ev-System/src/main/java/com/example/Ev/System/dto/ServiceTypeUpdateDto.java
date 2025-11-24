package com.example.Ev.System.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceTypeUpdateDto {
    private String name;
    private String description;


    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @Min(value = 1, message = "Thời gian ước tính phải lớn hơn 0")
    private Integer durationEst;
}

