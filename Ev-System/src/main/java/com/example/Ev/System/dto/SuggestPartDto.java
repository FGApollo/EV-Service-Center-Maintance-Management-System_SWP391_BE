package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestPartDto {
    private Integer quantity;
    private int appointmentId;
    private String technician_note;
    private String status;
    private Double total_price;
    private int partId;
    private String part_name;
    private Double unit_price;
    private String part_description;
    private Integer part_Id;
}
