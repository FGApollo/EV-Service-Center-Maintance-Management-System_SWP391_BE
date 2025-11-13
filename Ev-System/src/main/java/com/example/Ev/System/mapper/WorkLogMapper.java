package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.WorkLogDto;
import com.example.Ev.System.entity.Worklog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface WorkLogMapper {

    // ✅ Entity → DTO
    @Mapping(target = "staffId", expression = "java(java.util.List.of(workLog.getStaff().getId()))")
    @Mapping(target = "user", source = "staff") // Uses UserMapper to map User → UserDto
    @Mapping(target = "appointmentId", source = "appointment.id")
    WorkLogDto toDto(Worklog workLog);

    // ✅ DTO → Entity (manual relations)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    Worklog toEntity(WorkLogDto workLogDto);

    // ✅ List<Entity> → List<DTO>
    List<WorkLogDto> toDtoList(List<Worklog> workLogs);

    // ✅ List<DTO> → List<Entity>
    List<Worklog> toEntityList(List<WorkLogDto> workLogDtos);
}
