package com.example.Ev.System.mappers;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.entity.ServiceAppointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    ServiceAppointment toEntity(AppointmentDto appointmentDto);
    AppointmentDto toDto(ServiceAppointment appointment);
}
