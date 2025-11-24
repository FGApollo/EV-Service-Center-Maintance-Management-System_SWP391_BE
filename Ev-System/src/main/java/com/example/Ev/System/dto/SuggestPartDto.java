package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestPartDto {
    private Integer quantity;
    private String technician_note;
    private String status;
    private Double part_price;
    private String part_name;
}
