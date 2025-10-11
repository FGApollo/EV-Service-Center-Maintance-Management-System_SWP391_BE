package com.example.Ev.System.service;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }


    public UserDto createUser(RegisterUserDto registerUserDto,
                              UriComponentsBuilder uriComponentsBuilder)
    {
        var user = userMapper.toEntity(registerUserDto);
        user.setRole("customer");
        user.setStatus("active");
        userRepository.save(user);
        var userDto = userMapper.toDTO(user);
        return userDto;
    }

    public List<UserDto> getAllByRole(String role)
    {
        List<User> userByRole = userRepository.findAllByRole(role);
        userMapper.toDTO(userByRole);
        return userMapper.toDTO(userByRole);
    }


}
