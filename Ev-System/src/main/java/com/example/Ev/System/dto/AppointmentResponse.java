package com.example.Ev.System.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
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

/* */