package com.example.Ev.System.service;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public UserDto createUser(RegisterUserDto registerUserDto
                              )
    {
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        var user = userMapper.toEntity2(registerUserDto);
        user.setRole("customer");
        user.setStatus("active");
        user.setCreatedAt(Instant.now());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
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
    public UserDto createEmployee(RegisterUserDto userDto,String role){
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        var user = userMapper.toEntity2(userDto);
        user.setRole(role);
        user.setStatus("active");
        user.setCreatedAt(Instant.now());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPasswordHash()));
        userRepository.save(user);
        var userDTO = userMapper.toDTO(user);
        return userDTO;
    }

    @Transactional
    public UserDto deleteAccount(Integer userID)
    {
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found with id: " + userID));
        user.setStatus("inactive");
        userRepository.save(user);
        UserDto userDTO = userMapper.toDTO(user);
        return userDTO;
    }


}
