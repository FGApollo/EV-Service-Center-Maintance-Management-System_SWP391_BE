package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String fullName;
    private String email;
    private String phone;
    private String password;
}
