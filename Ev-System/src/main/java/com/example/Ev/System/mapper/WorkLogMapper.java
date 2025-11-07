package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.WorkLogDto;
import com.example.Ev.System.entity.Worklog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkLogMapper {

    // ✅ Entity → DTO
    @Mapping(target = "staffId", expression = "java(java.util.List.of(workLog.getStaff().getId()))")
    @Mapping(target = "appointmentId", source = "appointment.id")
    WorkLogDto toDto(Worklog workLog);

    // ✅ DTO → Entity (we ignore these because service sets them manually)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    Worklog toEntity(WorkLogDto workLogDto);

    // ✅ List<Entity> → List<DTO>
    List<WorkLogDto> toDtoList(List<Worklog> workLogs);

    // (optional) ✅ List<DTO> → List<Entity>
    List<Worklog> toEntityList(List<WorkLogDto> workLogDtos);
    //Sai la do day
}
