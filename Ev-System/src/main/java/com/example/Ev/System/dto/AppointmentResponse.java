package com.example.Ev.System.dto;

import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
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
    private String phone;
    private String customerName;
    private String vehicleModel;
    private String serviceCenterName;
    private Instant appointmentDate;
    private List<String> serviceNames;
    private String status;
    private String email;
    private Vehicle vehicle;

    private String url;
    private String techIds;
    private List<UserDto> users;
    private List<String> checkList;
    private int total;

}
