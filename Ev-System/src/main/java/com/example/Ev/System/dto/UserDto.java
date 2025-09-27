package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;

}
