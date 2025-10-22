package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentDto {
    private Integer vehicleId;
    private Integer serviceCenterId;
    private Instant appointmentDate;
    private Set<Integer> serviceTypeIds;
}
