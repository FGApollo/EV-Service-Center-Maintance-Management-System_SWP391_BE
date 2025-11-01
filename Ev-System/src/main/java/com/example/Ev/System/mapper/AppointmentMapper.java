package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.entity.ServiceAppointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    // ENTITY → DTO
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "serviceCenterId", source = "serviceCenter.id")
    @Mapping(target = "serviceTypeIds", expression = "java(mapServiceTypeIds(serviceAppointment))")
    AppointmentDto toDto(ServiceAppointment serviceAppointment);

    // DTO → ENTITY
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "serviceCenter.id", source = "serviceCenterId")
    @Mapping(target = "serviceTypes", ignore = true) // handle manually if needed
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "maintenanceRecords", ignore = true)
    @Mapping(target = "staffAssignments", ignore = true)
    @Mapping(target = "worklogs", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    ServiceAppointment toEntity(AppointmentDto appointmentDto);

    List<AppointmentDto> toDtoList(List<ServiceAppointment> appointments);
    List<ServiceAppointment> toEntityList(List<AppointmentDto> appointmentDtos);

    // Helper method: map service types to their IDs
    default Set<Integer> mapServiceTypeIds(ServiceAppointment serviceAppointment) {
        if (serviceAppointment.getServiceTypes() == null) {
            return null;
        }
        return serviceAppointment.getServiceTypes()
                .stream()
                .map(serviceType -> serviceType.getId())
                .collect(Collectors.toSet());
    }
}
