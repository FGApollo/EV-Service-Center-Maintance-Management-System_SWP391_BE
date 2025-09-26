package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
}
