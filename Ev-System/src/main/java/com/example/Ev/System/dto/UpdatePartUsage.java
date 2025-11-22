package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class UpdatePartUsage {
    private int status;
    private Integer partId;
    private Integer centerId;
    private Integer recordId;
    private int appointmentId;
    private Integer quantityUsed;
}
