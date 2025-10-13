package com.example.Ev.System.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class AppointmentResponse {
    private Integer appointmentId;
    private String customerName;
    private String vehicleModel;
    private String serviceCenterName;
    private Instant appointmentDate;
    private List<String> serviceNames;

    private String status;

    public AppointmentResponse(Integer appointmentId, String customerName, String vehicleModel, String serviceCenterName, Instant appointmentDate, List<String> serviceNames,  String status) {
        this.appointmentId = appointmentId;
        this.customerName = customerName;
        this.vehicleModel = vehicleModel;
        this.serviceCenterName = serviceCenterName;
        this.appointmentDate = appointmentDate;
        this.serviceNames = serviceNames;

        this.status = status;
    }


}

