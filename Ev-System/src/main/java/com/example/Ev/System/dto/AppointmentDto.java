package com.example.Ev.System.dto;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.ServiceCenter;
import com.example.Ev.System.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentDto {
    private Integer vehicleId;
    private Integer serviceCenterId;
    private Instant appointmentDate;
    private List<Integer> serviceTypeIds;
}
