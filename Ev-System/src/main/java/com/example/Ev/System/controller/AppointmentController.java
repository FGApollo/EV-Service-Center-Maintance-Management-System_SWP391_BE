package com.example.Ev.System.controller;


import com.example.Ev.System.dto.*;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mapper.AppointmentMapper;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.ServiceAppointmentRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.service.*;
import com.example.Ev.System.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    private final UserService userService;
    private final StaffAppointmentService staffAppointmentService;
    private final MaintenanceReminderCreationService maintenanceReminderCreationService;
    private final ServiceAppointmentRepository serviceAppointmentRepository;
    private final UserMapper userMapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentStatusService appointmentStatusService, ServiceAppointmentService serviceAppointmentService, MaintenanceRecordService maintenanceRecordService, WorkLogService workLogService, AppointmentMapper appointmentMapper, UserService userService, StaffAppointmentService staffAppointmentService, MaintenanceReminderCreationService maintenanceReminderCreationService, ServiceAppointmentRepository serviceAppointmentRepository, UserMapper userMapper) {
        this.appointmentService = appointmentService;
        this.appointmentStatusService = appointmentStatusService;
        this.serviceAppointmentService = serviceAppointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.workLogService = workLogService;
        this.appointmentMapper = appointmentMapper;
        this.userService = userService;
        this.staffAppointmentService = staffAppointmentService;
        this.maintenanceReminderCreationService = maintenanceReminderCreationService;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
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
    public List<AppointmentStatusDTO> getUserAppointment(Authentication authentication){
        return appointmentStatusService.getUserAppointment(authentication.getName());
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    @Transactional
    public ResponseEntity<AppointmentResponse> acceptAppointment(
            @PathVariable Integer id ,Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointment = serviceAppointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if (!appointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }


        ServiceAppointment updatedAppointment = serviceAppointmentService.acceptAppointment(id);
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
        //da test dc
    }


    @PutMapping("/{id}/cancel")
    @Transactional
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Integer id,Authentication authentication) //bo text vao body , chu k phai json , json la 1 class
    {

        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointment = serviceAppointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if (!appointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }

        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"cancelled");
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/inProgress")
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    @Transactional
    public ResponseEntity<AppointmentResponse> inProgressAppointment(
            @PathVariable Integer id,
            @RequestBody List<Integer> staffIds,Authentication authentication) //bo text vao body , chu k phai json , json la 1 class
    {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointment = serviceAppointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if (!appointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }

        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"in_progress");
        List<StaffAssignment> assignments = staffAppointmentService
                .assignTechnicians(id, staffIds, "notes",authentication);
        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(id);
        AppointmentResponse response = appointmentMapper.toResponse(updatedAppointment);
        String sId = staffIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        response.setTechIds(sId);

        List<User> techUsers = userService.getAllById(staffIdList);
        List<UserDto> techDto = userMapper.toDTOList(techUsers);
        response.setUsers(techDto);

        return ResponseEntity.ok(response);
        //Da xong
        //Todo : Thay vi tra ve full ServiceAppointment => Tra ve DTO
    }

    @PutMapping("/{id}/done")
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    @Transactional
    public ResponseEntity<AppointmentResponse> doneAppointment(
            @PathVariable Integer id , @RequestBody MaintainanceRecordDto maintainanceRecordDto,
            Authentication authentication) //bo text vao body , chu k phai json , json la 1 class
    {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointment = serviceAppointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if (!appointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }


        ServiceAppointment updatedAppointment = serviceAppointmentService.updateAppointment(id,"completed");//nho chuyen thanh done
        boolean recordExists = maintenanceRecordService.findMaintainanceRecordByAppointmentId(id);
        if(recordExists) {
            maintenanceRecordService.updateMaintainanceRecord(updatedAppointment.getId(), maintainanceRecordDto, 1,authentication); // Phai them record moi dc done
        //khong ma giao , da hoat dong duoc , chi hoat dong duoc 1 lan dau tien
        }
        else {
            maintenanceRecordService.recordMaintenance(id, maintainanceRecordDto,authentication);
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
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    @Transactional
    public List<AppointmentResponse> getAppointmentsByStatus(
            @PathVariable String status,
            Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        // Get all appointments by status + center
        List<ServiceAppointment> appointments =
                serviceAppointmentService.getStatusAppointments(status, centerId);

        List<AppointmentResponse> responses = new ArrayList<>();

        for (ServiceAppointment appointment : appointments) {
            AppointmentResponse response = appointmentMapper.toResponse(appointment);

            // --- Staff / Technician Info ---
            List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(appointment.getId());
            if (staffIdList != null && !staffIdList.isEmpty()) {
                String sId = staffIdList.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                response.setTechIds(sId);

                List<User> techUsers = userService.getAllById(staffIdList);
                List<UserDto> techDto = userMapper.toDTOList(techUsers);
                response.setUsers(techDto);
            } else {
                response.setUsers(Collections.emptyList());
                response.setTechIds("");
            }

            // --- Service Names & Total ---
            if (appointment.getServiceTypes() != null) {
                List<String> serviceNames = appointment.getServiceTypes().stream()
                        .map(ServiceType::getName)
                        .collect(Collectors.toList());
                response.setServiceNames(serviceNames);

                int total = appointment.getServiceTypes().stream()
                        .mapToInt(serviceType -> serviceType.getPrice().intValue())
                        .sum();
                response.setTotal(total);
            }

            MaintenanceRecord maintenanceRecord = maintenanceRecordService.getAllByAppointmentId(appointment.getId());
            if (maintenanceRecord != null && maintenanceRecord.getChecklist() != null) {
                List<String> checklist = List.of(maintenanceRecord.getChecklist().split("\\s*,\\s*"));
                response.setCheckList(checklist);
            }

            responses.add(response);
        }

        return responses;
    }


    @GetMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    @Transactional
    public AppointmentResponse getAppointmentsByDone(
            Authentication authentication,
            @PathVariable Integer id
            ) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointment = serviceAppointmentRepository.findById(id).orElse(null);

        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if (!appointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }

        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(id);
        ServiceAppointment updatedAppointment = serviceAppointmentRepository.findById(id).orElse(null);
        AppointmentResponse response = appointmentMapper.toResponse(updatedAppointment);
        String sId = staffIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        response.setTechIds(sId);

        if (staffIdList != null && !staffIdList.isEmpty()) {
            List<User> techUsers = userService.getAllById(staffIdList);
            List<UserDto> techDto = userMapper.toDTOList(techUsers);
            response.setUsers(techDto);
        } else {
            response.setUsers(Collections.emptyList());
        }
        if (updatedAppointment != null && updatedAppointment.getServiceTypes() != null) {
            List<String> serviceNames = updatedAppointment.getServiceTypes().stream()
                    .map(serviceType -> serviceType.getName())
                    .collect(Collectors.toList());
            response.setServiceNames(serviceNames);
        }

        int total = 0;
        Set<ServiceType> serviceTypes = updatedAppointment.getServiceTypes();
        for (ServiceType serviceType : serviceTypes) {
            total += serviceType.getPrice().intValue();
        }
        response.setTotal(total);


        MaintenanceRecord maintenanceRecord = maintenanceRecordService.getAllByAppointmentId(id);
        if (maintenanceRecord != null && maintenanceRecord.getChecklist() != null) {
            // Split comma-separated checklist into list (if stored like "Brake, Battery")
            List<String> checklist = List.of(maintenanceRecord.getChecklist().split("\\s*,\\s*"));
            response.setCheckList(checklist);
        }
        return response;
        //moi
    }

    @GetMapping("/staff")
    @PreAuthorize("hasAnyAuthority('staff', 'manager')")
    @Transactional
    public ResponseEntity<List<AppointmentResponse>> findAllByStaffId(@RequestParam Integer id) {
        List<ServiceAppointment> appointments = serviceAppointmentService.getAppointmentsByStaffId(id);
        return ResponseEntity.ok(appointmentMapper.toResponseList(appointments));
    }

    @GetMapping("/all")
    public List<AppointmentAllFieldsDto> getAllAppointment(){
        return serviceAppointmentService.getAllAppointment();
    }
}
