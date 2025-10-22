package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.PartUsageDto;
import com.example.Ev.System.entity.PartUsage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PartUsageMapper {

    // ✅ DTO → Entity (ignore relationships that are set manually)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "record", ignore = true)
    @Mapping(target = "part", ignore = true)
    PartUsage toEntity(PartUsageDto dto);

    // ✅ Entity → DTO (extract only part.id)
    @Mapping(source = "part.id", target = "partId")
    PartUsageDto toDto(PartUsage entity);

    // ✅ Optional: update existing entity from DTO (if you reuse objects)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "record", ignore = true)
    @Mapping(target = "part", ignore = true)
    void updatePartUsageFromDto(PartUsageDto dto, @MappingTarget PartUsage entity);
}
