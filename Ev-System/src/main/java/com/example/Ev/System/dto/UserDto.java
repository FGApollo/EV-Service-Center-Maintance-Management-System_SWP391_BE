package com.example.Ev.System.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String role;

    List<VehicleRespone> vehicles;
}
