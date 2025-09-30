package com.example.Ev.System.controller;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.service.ServiceAppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final ServiceAppointmentService appointmentService;
    public AppointmentController(ServiceAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceAppointment> acceptAppointment(
            @PathVariable Integer id) {
        ServiceAppointment updatedAppointment = appointmentService.acceptAppointment(id);
        return ResponseEntity.ok(updatedAppointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceAppointment> updateAppointment(
            @PathVariable Integer id,
            @RequestBody String status) {
        ServiceAppointment updatedAppointment = appointmentService.updateAppointment(id,status);
        return ResponseEntity.ok(updatedAppointment);
    }




}
