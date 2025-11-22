package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartQuantityDto {
    private Integer partId;
    private String partName;
    private Integer quantity;
}
