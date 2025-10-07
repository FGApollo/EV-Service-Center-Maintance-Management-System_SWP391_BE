package com.example.Ev.System.controller;

import com.example.Ev.System.dto.UserProfileResponse;
import com.example.Ev.System.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Authenticator;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final AuthService authService;

    public ProfileController (AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public UserProfileResponse getProfile(Authentication authentication){
        String email = authentication.getName();
        return authService.getProfile(email);
    }

}
