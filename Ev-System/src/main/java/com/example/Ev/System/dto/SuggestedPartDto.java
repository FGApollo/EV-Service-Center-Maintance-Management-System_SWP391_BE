package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedPartDto {
    private Integer partId;
    private String partName;
    private String partDescription;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}
