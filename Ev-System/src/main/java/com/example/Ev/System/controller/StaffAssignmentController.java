package com.example.Ev.System.controller;

import com.example.Ev.System.dto.StaffAssignmentDto;
import com.example.Ev.System.dto.StaffAssignmentRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
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
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    public ResponseEntity<List<StaffAssignmentDto>> assignTechnicians(
            @PathVariable Integer appointmentId,
            @RequestBody StaffAssignmentRequest request,
            Authentication authentication
    ) {
        List<StaffAssignmentDto> assignmentDtos = staffAppointmentService.assignTechniciansDto(
                appointmentId,
                request.getStaffIds(),
                request.getNotes(),
                authentication
        );
        return ResponseEntity.ok(assignmentDtos);
    }


    @GetMapping("/free")
    @PreAuthorize("hasAnyAuthority('manager', 'staff')")
    public ResponseEntity<List<StaffAssignmentDto>> findFreeStaff(Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        Integer centerId = currentUser.getServiceCenter().getId();
        List<User> freeTechnicians = staffAppointmentService.getFreeTechnician(centerId, "in_progress");
        List<StaffAssignmentDto> dtos = staffAssignmentMapper.toDtoList(freeTechnicians);

        return ResponseEntity.ok(dtos);
    }


}
