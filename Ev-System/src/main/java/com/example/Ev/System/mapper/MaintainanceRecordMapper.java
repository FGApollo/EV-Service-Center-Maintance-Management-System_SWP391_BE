package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.MaintenanceRecord;
import org.mapstruct.*;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { PartUsageMapper.class })
public interface MaintainanceRecordMapper {

    // 🔹 ENTITY → DTO
    @Mappings({
            @Mapping(source = "partUsages", target = "partsUsed"),
            @Mapping(target = "staffIds", expression = "java(parseStaffIds(record.getTechnicianIds()))")
    })
    MaintainanceRecordDto toDTO(MaintenanceRecord record);

    // 🔹 DTO → ENTITY
    @Mappings({
            @Mapping(source = "partsUsed", target = "partUsages"),
            @Mapping(target = "appointment", ignore = true),  // handled manually
            @Mapping(target = "technicianIds", expression = "java(joinStaffIds(dto.getStaffIds()))"),
            @Mapping(target = "startTime", ignore = true),
            @Mapping(target = "endTime", ignore = true)
    })
    MaintenanceRecord toEntity(MaintainanceRecordDto dto);

    // 🔹 Convert comma-separated technician IDs → List<Integer>
    default List<Integer> parseStaffIds(String technicianIds) {
        if (technicianIds == null || technicianIds.isBlank()) return Collections.emptyList();
        return Arrays.stream(technicianIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    // 🔹 Convert List<Integer> → comma-separated String
    default String joinStaffIds(List<Integer> staffIds) {
        if (staffIds == null || staffIds.isEmpty()) return null;
        return staffIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
