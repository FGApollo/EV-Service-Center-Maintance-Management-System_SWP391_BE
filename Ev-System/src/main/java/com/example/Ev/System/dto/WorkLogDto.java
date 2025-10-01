package com.example.Ev.System.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkLogDto {
    private Integer staffId;
    private Integer appointmentId;
    private Double hoursSpent;
    private String tasksDone;
}
