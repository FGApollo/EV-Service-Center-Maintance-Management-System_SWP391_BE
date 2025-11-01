package com.example.Ev.System.controller;

import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.repository.StaffAssignmentRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.service.StaffAppointmentService;
import com.example.Ev.System.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class StaffAssignmentController {
    private final StaffAppointmentService staffAppointmentService;
    private final UserService userService;
    private final UserRepository userRepository;

    public StaffAssignmentController(StaffAppointmentService staffAppointmentService, UserService userService, UserRepository userRepository) {
        this.staffAppointmentService = staffAppointmentService;
        this.userService = userService;
        this.userRepository = userRepository;
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

    @GetMapping("/free")
    public List<User> findFreeStaff(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int id = user.getServiceCenter().getId();
        Integer centerId = user.getServiceCenter().getId();
        return staffAppointmentService.getFreeTechnician(centerId,"in_progress");//lay in_progress vi bo no vo de tim busy employeed trc
    }
    //da xong , chi hien free cua thg manager vs center cua no

}
