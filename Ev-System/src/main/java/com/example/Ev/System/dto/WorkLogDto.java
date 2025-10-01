package com.example.Ev.System.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WorkLogDto {
    private Integer staffId;
    private Integer appointmentId;
    private BigDecimal hoursSpent;
    private String tasksDone;
}
