package com.example.Ev.System.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LoginResponse {
    private String token;
    private String role;
    private String fullName;
    private Integer id;
    private Integer centerId;
    private String phone;
}
