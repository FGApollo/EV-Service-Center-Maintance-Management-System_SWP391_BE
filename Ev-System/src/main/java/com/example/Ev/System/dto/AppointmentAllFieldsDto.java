package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentAllFieldsDto {
    private Integer appointmentId;
    private Integer customerId;
    private Integer vehicleId;
    private Integer centerId;
    private Instant appoimentDate;
    private String status;
    private Instant createAt;
    private String fullName;
    private String email;
    private String phone;
    private String serviceType;
}
