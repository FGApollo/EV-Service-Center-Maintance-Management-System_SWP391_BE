package com.example.Ev.System.dto;

import lombok.Data;

@Data
public class StaffAssignmentDto {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private boolean isWorking;
}
