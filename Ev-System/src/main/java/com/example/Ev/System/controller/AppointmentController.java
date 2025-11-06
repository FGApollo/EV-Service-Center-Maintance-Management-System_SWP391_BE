package com.example.Ev.System.controller;


import com.example.Ev.System.dto.*;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.AppointmentMapper;
import com.example.Ev.System.repository.ServiceAppointmentRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private final MaintenanceReminderCreationService maintenanceReminderCreationService;
    private final ServiceAppointmentRepository serviceAppointmentRepository;

    public AppointmentController(AppointmentService appointmentService, AppointmentStatusService appointmentStatusService, ServiceAppointmentService serviceAppointmentService, MaintenanceRecordService maintenanceRecordService, WorkLogService workLogService, AppointmentMapper appointmentMapper, UserRepository userRepository, StaffAppointmentService staffAppointmentService, MaintenanceReminderCreationService maintenanceReminderCreationService, ServiceAppointmentRepository serviceAppointmentRepository) {
        this.appointmentService = appointmentService;
        this.appointmentStatusService = appointmentStatusService;
        this.serviceAppointmentService = serviceAppointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.workLogService = workLogService;
        this.appointmentMapper = appointmentMapper;
        this.userRepository = userRepository;
        this.staffAppointmentService = staffAppointmentService;
        this.maintenanceReminderCreationService = maintenanceReminderCreationService;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
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
    @Transactional
    public ResponseEntity<AppointmentResponse> acceptAppointment(
            @PathVariable Integer id ) {
        ServiceAppointment updatedAppointment = serviceAppointmentService.acceptAppointment(id);
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
        //da test dc
    }


    @PutMapping("/{id}/cancel")
    @Transactional
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Integer id) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"cancelled");
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/inProgress")
    @Transactional
    public ResponseEntity<AppointmentResponse> inProgressAppointment(
            @PathVariable Integer id,
            @RequestBody List<Integer> staffIds) //bo text vao body , chu k phai json , json la 1 class
    {
        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"in_progress");
        List<StaffAssignment> assignments = staffAppointmentService
                .assignTechnicians(id, staffIds, "notes");
        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(id);
        AppointmentResponse response = appointmentMapper.toResponse(updatedAppointment);
        String sId = staffIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        response.setTechIds(sId);
        return ResponseEntity.ok(response);
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

        maintenanceReminderCreationService.createReminderForAppointmentIfDone(id);

        ServiceAppointment refreshed = serviceAppointmentService.getAppointmentWithAllDetails(id);

        AppointmentResponse response = appointmentMapper.toResponse(updatedAppointment);
        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(id);
        String sId = staffIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        response.setTechIds(sId);
        workLogService.autoCreateWorkLog(id);
        return ResponseEntity.ok(response);
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

    @GetMapping("/appointments/status/done")
    @Transactional
    public AppointmentResponse getAppointmentsByDone(
            @PathVariable String status,
            Authentication authentication,
            @PathVariable Integer id
            ) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        Integer centerId = currentUser.getServiceCenter().getId();
        List<ServiceAppointment> appointments =
                serviceAppointmentService.getStatusAppointments(status, centerId);
        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(id);
        ServiceAppointment updatedAppointment = serviceAppointmentRepository.findById(id).orElse(null);
        AppointmentResponse response = appointmentMapper.toResponse(updatedAppointment);
        String sId = staffIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        response.setTechIds(sId);
        return response;
        //moi
    }

    @GetMapping("/staff")
    @Transactional
    public ResponseEntity<List<AppointmentDto>> findAllByStaffId(@RequestParam Integer id) {
        List<ServiceAppointment> appointments = serviceAppointmentService.getAppointmentsByStaffId(id);
        return ResponseEntity.ok(appointmentMapper.toDtoList(appointments));
    }

    @GetMapping("/all")
    public List<AppointmentAllFieldsDto> getAllAppointment(){
        return serviceAppointmentService.getAllAppointment();
    }
}
