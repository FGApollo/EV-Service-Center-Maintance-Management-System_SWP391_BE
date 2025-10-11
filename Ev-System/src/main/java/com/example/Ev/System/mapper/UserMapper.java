package com.example.Ev.System.mapper;


import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDTO(User user);
    User toEntity(RegisterUserDto registerUserDTO);
    List<UserDto> toDTO(List<User> users);
}
