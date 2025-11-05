package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.StaffAssignmentDto;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffAssignmentMapper {

    // Simple conversion from entity → DTO
    @Mapping(target = "isWorking", ignore = true) // we'll set manually
    StaffAssignmentDto toDto(User user);

    List<StaffAssignmentDto> toDtoList(List<User> users);

    // Manual helper method: to set the working flag dynamically
    default StaffAssignmentDto toDtoWithStatus(User user, boolean isWorking) {
        StaffAssignmentDto dto = toDto(user);
        dto.setWorking(isWorking);
        return dto;
    }
}
