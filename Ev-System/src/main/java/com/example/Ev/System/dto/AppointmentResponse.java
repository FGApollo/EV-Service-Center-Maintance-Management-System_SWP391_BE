package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Integer appointmentId;
    private String customerName;
    private String vehicleModel;
    private String serviceCenterName;
    private Instant appointmentDate;
    private List<String> serviceNames;
    private String status;


    /*@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAppointmentDto {
    private Integer appointmentId;
    private LocalDateTime appointmentDate;
    private String status;
    private String serviceCenterName;
    private String vehicleModel;
}
*/




}

/* */