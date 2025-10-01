package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.Maintenancerecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MaintainanceRecordMapper {
    MaintainanceRecordDto toDTO(Maintenancerecord maintenanceRecord);
    Maintenancerecord toEntity(MaintainanceRecordDto maintainanceRecordDto);
}
