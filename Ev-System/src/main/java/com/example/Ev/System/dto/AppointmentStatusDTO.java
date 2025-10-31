package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusDTO {
    private Integer appointmentId;
    private Instant appointmentDate;
    private String status;
    private String ServiceCenterName;
    private String VehicleModel;
    private String ServiceTypeName;
    private int cost;
}
