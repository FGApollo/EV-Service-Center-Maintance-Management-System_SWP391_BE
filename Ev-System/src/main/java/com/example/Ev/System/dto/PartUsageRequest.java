package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class PartUsageRequest {
    private Integer partId;
    private Integer quantityUsed;
}
