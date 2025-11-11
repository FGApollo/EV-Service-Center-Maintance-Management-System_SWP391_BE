package com.example.Ev.System.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Data
public class StaffAssignmentRequest {
    private String notes;
    List<Integer> staffIds;

}
