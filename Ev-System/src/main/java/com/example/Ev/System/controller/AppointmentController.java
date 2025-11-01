package com.example.Ev.System.controller;


import com.example.Ev.System.dto.*;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.AppointmentMapper;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.service.*;
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
    private final ServiceAppointmentService serviceAppointmentService;
    private final MaintenanceRecordService maintenanceRecordService;
    private final WorkLogService workLogService;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;

    public AppointmentController(AppointmentService appointmentService, AppointmentStatusService appointmentStatusService, ServiceAppointmentService serviceAppointmentService, MaintenanceRecordService maintenanceRecordService, WorkLogService workLogService, AppointmentMapper appointmentMapper, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.appointmentStatusService = appointmentStatusService;
        this.serviceAppointmentService = serviceAppointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.workLogService = workLogService;
        this.appointmentMapper = appointmentMapper;
        this.userRepository = userRepository;
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

    @PutMapping("/{id}/accept")
    public ResponseEntity<AppointmentDto> acceptAppointment(
            @PathVariable Integer id ) {
        ServiceAppointment updatedAppointment = serviceAppointmentService.acceptAppointment(id);
        return ResponseEntity.ok(appointmentMapper.toDto(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
        //da test dc
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(
            @PathVariable Integer id) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"cancelled");
        return ResponseEntity.ok(appointmentMapper.toDto(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/inProgress")
    public ResponseEntity<AppointmentDto> inProgressAppointment(
            @PathVariable Integer id) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"in_progress");
        return ResponseEntity.ok(appointmentMapper.toDto(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/done")
    public ResponseEntity<AppointmentDto> doneAppointment(
            @PathVariable Integer id , @RequestBody MaintainanceRecordDto maintainanceRecordDto ) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"completed");//nho chuyen thanh done
        maintenanceRecordService.updateMaintainanceRecord(id, maintainanceRecordDto,1); // Phai them record moi dc done
        workLogService.autoCreateWorkLog(id);
        return ResponseEntity.ok(appointmentMapper.toDto(updatedAppointment));
    }

    @GetMapping("/appointments/status/{status}")
    public List<AppointmentDto> getAppointmentsByStatus(
            @PathVariable String status,
            Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        Integer centerId = currentUser.getServiceCenter().getId();
        List<ServiceAppointment> appointments =
                serviceAppointmentService.getStatusAppointments(status, centerId);
        return appointmentMapper.toDtoList(appointments);
        //moi
    }

    @GetMapping("/staff")
    public ResponseEntity<List<AppointmentDto>> findAllByStaffId(@RequestParam Integer id) {
        List<ServiceAppointment> appointments = serviceAppointmentService.getAppointmentsByStaffId(id);
        return ResponseEntity.ok(appointmentMapper.toDtoList(appointments));
    }
}
