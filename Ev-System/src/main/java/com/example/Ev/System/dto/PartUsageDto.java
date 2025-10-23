package com.example.Ev.System.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartUsageDto {
    private Integer partId;
    private Integer quantityUsed;
    private Double unitCost;
}
