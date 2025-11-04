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
import org.springframework.transaction.annotation.Transactional;
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
    private final StaffAppointmentService staffAppointmentService;
    public AppointmentController(AppointmentService appointmentService, AppointmentStatusService appointmentStatusService, ServiceAppointmentService serviceAppointmentService, MaintenanceRecordService maintenanceRecordService, WorkLogService workLogService, AppointmentMapper appointmentMapper, UserRepository userRepository, StaffAppointmentService staffAppointmentService) {
        this.appointmentService = appointmentService;
        this.appointmentStatusService = appointmentStatusService;
        this.serviceAppointmentService = serviceAppointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.workLogService = workLogService;
        this.appointmentMapper = appointmentMapper;
        this.userRepository = userRepository;
        this.staffAppointmentService = staffAppointmentService;
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
    public ResponseEntity<AppointmentResponse> acceptAppointment(
            @PathVariable Integer id ) {
        ServiceAppointment updatedAppointment = serviceAppointmentService.acceptAppointment(id);
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
        //da test dc
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Integer id) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"cancelled");
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/inProgress")
    public ResponseEntity<AppointmentResponse> inProgressAppointment(
            @PathVariable Integer id) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"in_progress");
        staffAppointmentService.autoAssignTechnician(
                updatedAppointment.getId(),
                "Auto assign technician",
                updatedAppointment.getServiceCenter().getId(),
                "in_progress"
        );
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/done")
    @Transactional
    public ResponseEntity<AppointmentResponse> doneAppointment(
            @PathVariable Integer id , @RequestBody MaintainanceRecordDto maintainanceRecordDto ) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"completed");//nho chuyen thanh done
        boolean recordExists = maintenanceRecordService.findMaintainanceRecordByAppointmentId(id);
        if(recordExists) {
            maintenanceRecordService.updateMaintainanceRecord(updatedAppointment.getId(), maintainanceRecordDto, 1); // Phai them record moi dc done
        //khong ma giao , da hoat dong duoc , chi hoat dong duoc 1 lan dau tien
        }
        else {
            maintenanceRecordService.recordMaintenance(id, maintainanceRecordDto);
        }
        ServiceAppointment refreshed = serviceAppointmentService.getAppointmentWithAllDetails(id);
        workLogService.autoCreateWorkLog(id);
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
    }

    @GetMapping("/appointments/status/{status}")
    @Transactional
    public List<AppointmentResponse> getAppointmentsByStatus(
            @PathVariable String status,
            Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        Integer centerId = currentUser.getServiceCenter().getId();
        List<ServiceAppointment> appointments =
                serviceAppointmentService.getStatusAppointments(status, centerId);
        return appointmentMapper.toResponseList(appointments);
        //moi
    }

    @GetMapping("/staff")
    public ResponseEntity<List<AppointmentDto>> findAllByStaffId(@RequestParam Integer id) {
        List<ServiceAppointment> appointments = serviceAppointmentService.getAppointmentsByStaffId(id);
        return ResponseEntity.ok(appointmentMapper.toDtoList(appointments));
    }

    @GetMapping("/all")
    public List<AppointmentAllFieldsDto> getAllAppointment(){
        return serviceAppointmentService.getAllAppointment();
    }
}
