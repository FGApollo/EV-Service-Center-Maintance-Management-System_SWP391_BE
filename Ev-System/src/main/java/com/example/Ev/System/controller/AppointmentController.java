package com.example.Ev.System.controller;


import com.example.Ev.System.dto.*;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mapper.AppointmentMapper;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.ServiceAppointmentRepository;

import com.example.Ev.System.service.*;
import com.example.Ev.System.service.AppointmentService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")

public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentStatusService appointmentStatusService;
    private final ServiceAppointmentService serviceAppointmentService;
    private final MaintenanceRecordService maintenanceRecordService;
    private final WorkLogService workLogService;
    private final AppointmentMapper appointmentMapper;
    private final InvoiceService invoiceService;
    private final StaffAppointmentService staffAppointmentService;
    private final MaintenanceReminderCreationService maintenanceReminderCreationService;

    private final UserMapper userMapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentStatusService appointmentStatusService, ServiceAppointmentService serviceAppointmentService, MaintenanceRecordService maintenanceRecordService, WorkLogService workLogService, AppointmentMapper appointmentMapper, InvoiceService invoiceService, StaffAppointmentService staffAppointmentService, MaintenanceReminderCreationService maintenanceReminderCreationService, ServiceAppointmentRepository serviceAppointmentRepository, UserMapper userMapper) {
        this.appointmentService = appointmentService;
        this.appointmentStatusService = appointmentStatusService;
        this.serviceAppointmentService = serviceAppointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.workLogService = workLogService;
        this.appointmentMapper = appointmentMapper;
        this.invoiceService = invoiceService;
        this.staffAppointmentService = staffAppointmentService;
        this.maintenanceReminderCreationService = maintenanceReminderCreationService;
        this.userMapper = userMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {

        // Lấy email từ JWT (được lưu trong SecurityContext)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        AppointmentResponse response = appointmentService.createAppointment(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("(hasAuthority('customer'))")
    public List<AppointmentStatusDTO> getUserAppointment(Authentication authentication){
        return appointmentStatusService.getUserAppointment(authentication.getName());
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    @Transactional
    public ResponseEntity<Void> acceptAppointment(
            @PathVariable Integer id ,Authentication authentication) {
        serviceAppointmentService.validateAndGetAppointmentForCenter(authentication,id);
        ServiceAppointment updatedAppointment = serviceAppointmentService.acceptAppointment(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/cancel")
    @Transactional
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Integer id,Authentication authentication) //bo text vao body , chu k phai json , json la 1 class
    {
        serviceAppointmentService.validateAndGetAppointmentForCenter(authentication,id);
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"cancelled");
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{id}/waiting")
    @Transactional
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    public ResponseEntity<Void> waitingAppointment(
            @PathVariable Integer id,Authentication authentication)
    {
        serviceAppointmentService.validateAndGetAppointmentForCenter(authentication,id);
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"awaiting_pickup");
        if(serviceAppointmentService.checkInvoiceValid(updatedAppointment)) {
            invoiceService.createPartInvoice(id);
        }
        return ResponseEntity.noContent().build();
        //chua test
        //da xong
    }

    @PutMapping("/{id}/inProgress")
    @PreAuthorize("hasAnyAuthority('staff', 'manager', 'technician')")
    @Transactional
    public ResponseEntity<Void> inProgressAppointment(
            @PathVariable Integer id,
            Authentication authentication) {
        AppointmentResponse response =
                serviceAppointmentService.markAppointmentInProgress(id, authentication);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/done")
    @PreAuthorize("hasAnyAuthority('technician')")
    @Transactional
    public ResponseEntity<Void> doneAppointment(
            @PathVariable Integer id,
            @RequestBody MaintainanceRecordDto maintainanceRecordDto,
            Authentication authentication) {
        AppointmentResponse response =
                serviceAppointmentService.markAppointmentAsDone(id, maintainanceRecordDto, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/appointments/status/{status}")
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(
            @PathVariable String status,
            Authentication authentication) {
        List<AppointmentResponse> responses =
                serviceAppointmentService.getAppointmentsByStatusForCenter(status, authentication);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    @Transactional(readOnly = true)
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Integer id,
            Authentication authentication) {

        AppointmentResponse response = serviceAppointmentService.getAppointmentDetailsById(id, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/staff/{id}")
    @PreAuthorize("hasAnyAuthority('staff','technician', 'manager')")
    @Transactional
    public ResponseEntity<List<AppointmentResponse>> findAllByStaffId(@PathVariable Integer id) {
        List<ServiceAppointment> appointments = serviceAppointmentService.getAppointmentsByStaffId(id);
        return ResponseEntity.ok(appointmentMapper.toResponseList(appointments));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('staff', 'manager', 'admin')")
    public List<AppointmentAllFieldsDto> getAllAppointmentWithSpecificDetails(){
        return serviceAppointmentService.getAllAppointmentWithSpecificDetails();
    }

    @PutMapping("/{id}/handover")
    @PreAuthorize("hasAuthority('staff')")
    public ResponseEntity<AppointmentResponse> handoverVehicleOfAppointment(
            @PathVariable Integer id, Authentication authentication) {

        AppointmentResponse response = serviceAppointmentService.handoverVehicleOfAppointment(id, authentication);
        return ResponseEntity.ok(response);
    }

}
