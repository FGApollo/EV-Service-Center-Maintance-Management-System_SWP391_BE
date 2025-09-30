package com.example.Ev.System.controller;

import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.repository.StaffAssignmentRepository;
import com.example.Ev.System.service.StaffAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class StaffAssignmentController {
    private final StaffAppointmentService staffAppointmentService;

    @PostMapping("/{appointmentId}/staff/{staffId}")
    public ResponseEntity<StaffAssignment> assignTechnician(
            @PathVariable Integer appointmentId,
            @PathVariable List<Long> staffId,
            @RequestParam(defaultValue = "technician") String role,
            @RequestParam(required = false) String notes) {
        StaffAssignment assignment = staffAppointmentService
                .assignTechnician(appointmentId, staffId, role, notes);

        return ResponseEntity.ok(assignment);
    }
}
