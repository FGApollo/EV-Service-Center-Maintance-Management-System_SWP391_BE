package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.PartUsageDto;
import com.example.Ev.System.entity.PartUsage;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PartUsageMapper {

    // ✅ Map from DTO → Entity
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "record", ignore = true), // set manually later
            @Mapping(target = "part", ignore = true)    // set manually using repository
    })
    PartUsage toEntity(PartUsageDto dto);

    // ✅ Map from Entity → DTO
    @Mappings({
            @Mapping(source = "part.id", target = "partId")
    })
    PartUsageDto toDto(PartUsage entity);
}
