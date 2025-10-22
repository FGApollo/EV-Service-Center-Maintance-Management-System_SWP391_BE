package com.example.Ev.System.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppointmentRequest {

    @NotNull(message = "Vui lòng chọn xe cần đặt lịch.")
    private Integer vehicleId;

    @NotNull(message = "Vui lòng chọn trung tâm dịch vụ.")
    private Long serviceCenterId;

    @NotNull(message = "Vui lòng chọn thời gian đặt lịch.")
    private Instant appointmentDate;

    @NotNull(message = "Phải chọn ít nhất một dịch vụ.")
    private List<Integer> serviceTypeIds;
}