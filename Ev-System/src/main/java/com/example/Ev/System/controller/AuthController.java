package com.example.Ev.System.controller;

import com.example.Ev.System.dto.LoginRequest;
import com.example.Ev.System.dto.LoginResponse;
import com.example.Ev.System.dto.UpdateUserRequest;
import com.example.Ev.System.dto.UpdateUserResponse;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request) {

        User updatedUser = authService.updateUser(id, request);

        UpdateUserResponse response = new UpdateUserResponse();
        response.setEmail(updatedUser.getEmail());
        response.setFullName(updatedUser.getFullName());
        response.setPhone(updatedUser.getPhone());

        return ResponseEntity.ok(response);
    }
}
