package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.MaintenanceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaintainanceRecordMapper {
    @Mapping(target = "partsUsed", ignore = true)
    MaintainanceRecordDto toDTO(MaintenanceRecord maintenanceRecord);

    @Mapping(target = "partyusages", ignore = true) // handled manually
    @Mapping(target = "appointment", ignore = true) // handled elsewhere
    @Mapping(target = "technicianIds", ignore = true)
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    MaintenanceRecord toEntity(MaintainanceRecordDto maintainanceRecordDto);
}
