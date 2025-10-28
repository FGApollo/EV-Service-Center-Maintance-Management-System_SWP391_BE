package com.example.Ev.System.service;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
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
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
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
