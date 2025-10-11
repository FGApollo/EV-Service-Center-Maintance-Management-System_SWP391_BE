package com.example.Ev.System.controller;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.service.MaintenanceRecordService;
import com.example.Ev.System.service.ServiceAppointmentService;
import com.example.Ev.System.service.StaffAppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final ServiceAppointmentService appointmentService;
    private final MaintenanceRecordService maintenanceRecordService;
    private final StaffAppointmentService staffAppointmentService;
    public AppointmentController(ServiceAppointmentService appointmentService, MaintenanceRecordService maintenanceRecordService, StaffAppointmentService staffAppointmentService) {
        this.appointmentService = appointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.staffAppointmentService = staffAppointmentService;
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ServiceAppointment> acceptAppointment(
            @PathVariable Integer id ) {
        ServiceAppointment updatedAppointment = appointmentService.acceptAppointment(id);
        return ResponseEntity.ok(updatedAppointment);
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }


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
        ServiceAppointment updatedAppointment = appointmentService.updateAppointment(id,"in_progress");//nho chuyen thanh done
        maintenanceRecordService.recordMaintenance(id, maintainanceRecordDto); // Phai them record moi dc done
        return ResponseEntity.ok(updatedAppointment);
    }

    public ResponseEntity<List<ServiceAppointment>> findAllByStatus(@RequestParam String status) {
        return ResponseEntity.ok(appointmentService.getStatusAppointments(status));
    }

    public ResponseEntity<List<ServiceAppointment>> findAllByStaffId(@RequestParam Integer id) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByStaffId(id));
    }

}
