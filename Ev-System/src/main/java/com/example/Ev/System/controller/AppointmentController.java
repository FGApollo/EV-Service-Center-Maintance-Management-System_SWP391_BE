package com.example.Ev.System.controller;


import com.example.Ev.System.dto.AppointmentRequest;
import com.example.Ev.System.dto.AppointmentResponse;
import com.example.Ev.System.dto.AppointmentStatusDTO;
import com.example.Ev.System.service.AppointmentService;
import com.example.Ev.System.service.AppointmentStatusService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*@RestController
@RequestMapping("/api/appointments")
public class ServiceAppointmentController {

    @Autowired
    private ServiceAppointmentService appointmentService;

    @GetMapping
    public List<ServiceAppointmentDto> getUserAppointments(Authentication authentication) {
        return appointmentService.getUserAppointments(authentication.getName());
    }
}
*/
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentStatusService appointmentStatusService;

    public AppointmentController(AppointmentService appointmentService, AppointmentStatusService appointmentStatusService) {
        this.appointmentService = appointmentService;
        this.appointmentStatusService = appointmentStatusService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {

        // Lấy email từ JWT (được lưu trong SecurityContext)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        AppointmentResponse response = appointmentService.createAppointment(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<AppointmentStatusDTO> getUserAppointment(Authentication authentication){
        return appointmentStatusService.getUserAppointment(authentication.getName());
    }
}
