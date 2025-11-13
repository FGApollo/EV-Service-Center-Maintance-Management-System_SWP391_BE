package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.VehicleDto;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);


    VehicleDto toDto(Vehicle vehicle);

}
