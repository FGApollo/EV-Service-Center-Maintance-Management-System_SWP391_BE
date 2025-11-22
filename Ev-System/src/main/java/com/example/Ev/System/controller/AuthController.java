package com.example.Ev.System.controller;

import com.example.Ev.System.dto.*;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.service.AuthService;
import com.example.Ev.System.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;


    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request){
        return authService. login(request);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterUserDto registerUserDto)
    {
        UserDto userDTO = userService.createUser(registerUserDto);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateUserRequest request) {

        User updatedUser = authService.updateUser(id, request);

        UpdateUserResponse response = new UpdateUserResponse();
        response.setEmail(updatedUser.getEmail());
        response.setFullName(updatedUser.getFullName());
        response.setPhone(updatedUser.getPhone());

        return ResponseEntity.ok(response);
    }
}
