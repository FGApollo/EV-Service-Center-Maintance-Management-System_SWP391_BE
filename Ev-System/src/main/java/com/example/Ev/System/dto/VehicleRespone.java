package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRespone {
    private Integer vehicleId;
    private String model;
    private Integer year;
    private String vin;
    private String licensePlate;
    private String color;
    private String ownerName;
    private int maintenanceCount;
    private Instant closetTime;

    private List<String> maintenanceServices;
}
