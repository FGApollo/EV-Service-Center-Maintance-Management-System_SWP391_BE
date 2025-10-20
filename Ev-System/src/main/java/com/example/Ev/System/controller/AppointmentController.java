package com.example.Ev.System.controller;


import com.example.Ev.System.dto.AppointmentRequest;
import com.example.Ev.System.dto.AppointmentResponse;
import com.example.Ev.System.dto.AppointmentStatusDTO;
import com.example.Ev.System.service.AppointmentService;
import com.example.Ev.System.service.AppointmentStatusService;
import jakarta.validation.Valid;
import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.service.MaintenanceRecordService;
import com.example.Ev.System.service.ServiceAppointmentService;
import com.example.Ev.System.service.StaffAppointmentService;
import com.example.Ev.System.service.WorkLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final ServiceAppointmentService appointmentService;
    private final MaintenanceRecordService maintenanceRecordService;
    private final StaffAppointmentService staffAppointmentService;
    private final WorkLogService workLogService;

    private final AppointmentService appointmentService;
    private final AppointmentStatusService appointmentStatusService;

    public AppointmentController(AppointmentService appointmentService, AppointmentStatusService appointmentStatusService) {
        this.appointmentService = appointmentService;
        this.appointmentStatusService = appointmentStatusService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.staffAppointmentService = staffAppointmentService;
        this.workLogService = workLogService;
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ServiceAppointment> acceptAppointment(
            @PathVariable Integer id ) {
        ServiceAppointment updatedAppointment = appointmentService.acceptAppointment(id);
        return ResponseEntity.ok(updatedAppointment);
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {

        // Lấy email từ JWT (được lưu trong SecurityContext)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ServiceAppointment> cancelAppointment(
            @PathVariable Integer id) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = appointmentService.updateAppointment(id,"cancelled");
        return ResponseEntity.ok(updatedAppointment);
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/done")
    public ResponseEntity<ServiceAppointment> doneAppointment(
            @PathVariable Integer id , @RequestBody MaintainanceRecordDto maintainanceRecordDto ) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = appointmentService.updateAppointment(id,"completed");//nho chuyen thanh done
        maintenanceRecordService.recordMaintenance(id, maintainanceRecordDto); // Phai them record moi dc done
        workLogService.autoCreateWorkLog(id);
        return ResponseEntity.ok(updatedAppointment);
    }

        AppointmentResponse response = appointmentService.createAppointment(request, email);
        return ResponseEntity.ok(response);
    public ResponseEntity<List<ServiceAppointment>> findAllByStatus(@RequestParam String status) {
        return ResponseEntity.ok(appointmentService.getStatusAppointments(status));
    }

    @GetMapping
    public List<AppointmentStatusDTO> getUserAppointment(Authentication authentication){
        return appointmentStatusService.getUserAppointment(authentication.getName());
    @GetMapping("/staff")
    public ResponseEntity<List<ServiceAppointment>> findAllByStaffId(@RequestParam Integer id) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByStaffId(id));
    }

}
