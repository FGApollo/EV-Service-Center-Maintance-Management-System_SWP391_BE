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
public class StaffAssignmentController {
    private final StaffAppointmentService staffAppointmentService;

    public StaffAssignmentController(StaffAppointmentService staffAppointmentService) {
        this.staffAppointmentService = staffAppointmentService;
    }

    @PutMapping("/{appointmentId}/staff")
    public ResponseEntity<List<StaffAssignment>> assignTechnicians(
            @PathVariable Integer appointmentId,
            @RequestBody List<Integer> staffIds
            ) {
        List<StaffAssignment> assignments = staffAppointmentService
                .assignTechnicians(appointmentId, staffIds, "notes");
        return ResponseEntity.ok(assignments);
        //Da xong
        //ToDO : Nen tra ve DTO
    }

}
