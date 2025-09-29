package com.example.Ev.System.controller;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
//    @PostMapping
//    public ResponseEntity<AppointmentDto> createAppointment(
//            @RequestBody AppointmentDto appointmentDto,
//            Authentication authentication) {
//        AppointmentDto savedAppointment = appointmentService.createAppointment(appointmentDto, authentication);
//        return ResponseEntity.ok(savedAppointment);
//    }
@PostMapping
public ResponseEntity<AppointmentDto> createAppointment(
        @RequestBody AppointmentDto appointmentDto) {
    AppointmentDto savedAppointment = appointmentService.createAppointment(appointmentDto);
    return ResponseEntity.ok(savedAppointment);
}
}
