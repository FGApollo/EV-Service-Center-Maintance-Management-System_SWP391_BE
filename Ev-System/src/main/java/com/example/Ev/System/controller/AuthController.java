package com.example.Ev.System.controller;

import com.example.Ev.System.dto.LoginRequest;
import com.example.Ev.System.dto.LoginResponse;
import com.example.Ev.System.dto.UpdateUserRequest;
import com.example.Ev.System.dto.UpdateUserResponse;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/user/update")
    public ResponseEntity<UpdateUserResponse> updateUser(@RequestBody UpdateUserRequest request) {
        User updatedUser = authService.updateUser(request);
        UpdateUserResponse response = new UpdateUserResponse();
        response.setEmail(updatedUser.getEmail());
        response.setFullName(updatedUser.getFullName());
        response.setPhone(updatedUser.getPhone());
        return ResponseEntity.ok(response);
    }
}
