package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.entity.ServiceAppointment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    ServiceAppointment toEntity(AppointmentDto appointmentDto);
    AppointmentDto toDto(ServiceAppointment appointment);

    List<AppointmentDto> toDtoList(List<ServiceAppointment> appointments);
    List<ServiceAppointment> toEntityList(List<AppointmentDto> appointmentDtos);
}
