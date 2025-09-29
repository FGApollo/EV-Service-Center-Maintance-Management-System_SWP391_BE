package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class UpdateUserResponse {
    private String email;
    private String fullName;
    private String phone;
}
