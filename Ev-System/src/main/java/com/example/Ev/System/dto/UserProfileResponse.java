package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String status;


}
