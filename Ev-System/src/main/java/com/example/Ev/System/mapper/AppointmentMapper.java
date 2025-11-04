package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.dto.AppointmentResponse;
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

    // ENTITY → RESPONSE DTO
    @Mapping(target = "appointmentId", source = "id")
    @Mapping(target = "customerName", source = "customer.fullName") // Assuming User entity has 'fullName'
    @Mapping(target = "phone", source = "customer.phone")
    @Mapping(target = "vehicleModel", source = "vehicle.model") // Assuming Vehicle entity has 'model'
    @Mapping(target = "serviceCenterName", source = "serviceCenter.name") // Assuming ServiceCenter entity has 'name'
    @Mapping(target = "appointmentDate", source = "appointmentDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "serviceNames", expression = "java(mapServiceNames(serviceAppointment))")
    AppointmentResponse toResponse(ServiceAppointment serviceAppointment);

    List<AppointmentResponse> toResponseList(List<ServiceAppointment> appointments);

    // Helper: extract all service type names
    default List<String> mapServiceNames(ServiceAppointment serviceAppointment) {
        if (serviceAppointment.getServiceTypes() == null) {
            return null;
        }
        return serviceAppointment.getServiceTypes()
                .stream()
                .map(serviceType -> serviceType.getName()) // assuming field is 'serviceName'
                .collect(Collectors.toList());
    }
}
