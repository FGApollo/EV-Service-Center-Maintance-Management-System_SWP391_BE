package com.example.Ev.System.service;

import com.example.Ev.System.dto.LoginRequest;
import com.example.Ev.System.dto.LoginResponse;
import com.example.Ev.System.dto.UpdateUserRequest;
import com.example.Ev.System.dto.UserProfileResponse;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request){
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if(userOpt.isEmpty()){
            throw new NotFoundException("User not found");
        }

        User user = userOpt.get();
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new BadRequestException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setFullName(user.getFullName());

        return response;
    }


    //Update User
    @Transactional
    public User updateUser(UpdateUserRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = userOpt.get();
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }



        return userRepository.save(user);
    }

    public UserProfileResponse getProfile(String email){
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }

        UserProfileResponse profile = new UserProfileResponse();
        profile.setFullName(user.get().getFullName());
        profile.setEmail(user.get().getEmail());
        profile.setPhone(user.get().getPhone());
        profile.setRole(user.get().getRole());

        return profile;
    }
}
