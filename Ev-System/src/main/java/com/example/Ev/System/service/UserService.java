package com.example.Ev.System.service;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Transactional
    public UserDto createUser(RegisterUserDto registerUserDto,
                              UriComponentsBuilder uriComponentsBuilder)
    {
        var user = userMapper.toEntity2(registerUserDto);
        user.setRole("customer");
        user.setStatus("active");
        userRepository.save(user);
        var userDto = userMapper.toDTO(user);
        return userDto;
    }

    public List<UserDto> getAllByRole(String role)
    {
        List<User> userByRole = userRepository.findAllByRole(role);
        return userMapper.toDTOList(userByRole);
    }

    @Transactional
    public UserDto createEmployee(UserDto userDto,String role){
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        var user = userMapper.toEntity(userDto);
        user.setRole(role);
        user.setStatus("active");
        userRepository.save(user);
        var userDTO = userMapper.toDTO(user);
        return userDto;
    }

    public UserDto deleteAccount(Integer userID)
    {
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found with id: " + userID));
        user.setStatus("inactive");
        userRepository.save(user);
        UserDto userDTO = userMapper.toDTO(user);
        return userDTO;
    }


}
