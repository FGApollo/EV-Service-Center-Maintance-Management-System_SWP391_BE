package com.example.Ev.System.controller;

import com.example.Ev.System.dto.StaffAssignmentDto;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.StaffAssignmentMapper;
import com.example.Ev.System.repository.StaffAssignmentRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.service.StaffAppointmentService;
import com.example.Ev.System.service.UserService;
import jakarta.transaction.Transactional;
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
    private final StaffAssignmentMapper staffAssignmentMapper;

    public StaffAssignmentController(StaffAppointmentService staffAppointmentService, UserService userService, UserRepository userRepository, StaffAssignmentRepository staffAssignmentRepository, StaffAssignmentMapper staffAssignmentMapper) {
        this.staffAppointmentService = staffAppointmentService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.staffAssignmentMapper = staffAssignmentMapper;
    }

    @PutMapping("/{appointmentId}/staff")
    public ResponseEntity<List<StaffAssignment>> assignTechnicians(
            @PathVariable Integer appointmentId,
            @RequestBody List<Integer> staffIds
            ) {
        List<StaffAssignment> assignments = staffAppointmentService
                .assignTechnicians(appointmentId, staffIds, "notes");
        List<StaffAssignmentDto> assignmentDtos = assignments.stream()
                .map(a -> staffAssignmentMapper.toDtoWithStatus(
                        a.getStaff(),
                        true
                ))
                .toList();
        return ResponseEntity.ok(assignments);
        //Da xong
        //ToDO : Nen tra ve DTO
    }

    @GetMapping("/free")
    @Transactional
    public List<StaffAssignmentDto> findFreeStaff(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int id = user.getServiceCenter().getId();
        Integer centerId = user.getServiceCenter().getId();
        List<User> users = staffAppointmentService.getFreeTechnician(centerId,"in_progress");
        List<StaffAssignmentDto> staffAssignmentDtos = staffAssignmentMapper.toDtoList(users);
        return staffAssignmentDtos ;//lay in_progress vi bo no vo de tim busy employeed trc
    }
    //da xong , chi hien free cua thg manager vs center cua no

}
