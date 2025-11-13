package com.example.Ev.System.mapper;


import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "serviceCenter.id", target = "centerId")
    UserDto toDTO(User user);

    User toEntity(UserDto userDto);

    @Mapping(source = "password", target = "passwordHash")
    User toEntity2(RegisterUserDto registerUserDto);

    @IterableMapping(elementTargetType = UserDto.class)
    List<UserDto> toDTOList(List<User> users);
}
