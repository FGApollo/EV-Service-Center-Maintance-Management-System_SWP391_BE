package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.StaffAssignmentDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffAssignmentMapper {

    // Simple conversion from entity → DTO
    @Mapping(target = "working", ignore = true) // we'll set manually
    @Mapping(target = "appointmentId", ignore = true)
    StaffAssignmentDto toDto(User user);

    @Mapping(target = "appointmentId", ignore = true)
    @Mapping(target = "working", ignore = true)
    StaffAssignmentDto toDtoFromDto(UserDto userDto);

    List<StaffAssignmentDto> toDtoListFromUserDto(List<UserDto> userDtos);


    List<StaffAssignmentDto> toDtoList(List<User> users);

    default StaffAssignmentDto toDtoWithStatus(User user, boolean isWorking) {
        StaffAssignmentDto dto = toDto(user);
        dto.setWorking(isWorking);
        return dto;
    }
}
