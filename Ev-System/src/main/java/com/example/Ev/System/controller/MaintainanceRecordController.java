package com.example.Ev.System.controller;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.MaintainanceRecordMapper;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.service.MaintenanceRecordService;
import com.example.Ev.System.service.ServiceAppointmentService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/MaintainanceRecord")
public class MaintainanceRecordController {
    private final MaintenanceRecordService maintenanceRecordService;
    private final ServiceAppointmentService serviceAppointmentService;
    private final MaintainanceRecordMapper maintainanceRecordMapper;

    public MaintainanceRecordController(MaintenanceRecordService maintenanceRecordService, ServiceAppointmentService serviceAppointmentService, UserRepository userRepository, MaintainanceRecordMapper maintainanceRecordMapper) {
        this.maintenanceRecordService = maintenanceRecordService;
        this.serviceAppointmentService = serviceAppointmentService;
        this.maintainanceRecordMapper = maintainanceRecordMapper;
    }

    @PostMapping("/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('technician')")
    public ResponseEntity<MaintainanceRecordDto> createMaintenanceRecord(
            @PathVariable("appointmentId") Integer appointmentId,
            @RequestBody MaintainanceRecordDto maintainanceRecordDto,
            Authentication authentication) {
        if(maintenanceRecordService.findMaintainanceRecordByAppointmentId(appointmentId)){
            maintenanceRecordService.updateMaintainanceRecord(appointmentId, maintainanceRecordDto,0,authentication);
            System.out.println("Da chay update");
        }
        else {
            maintenanceRecordService.recordMaintenance(appointmentId, maintainanceRecordDto,authentication,0);
        }
        return ResponseEntity.ok(maintainanceRecordDto);
       //can test
        //da test xong
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
    public ResponseEntity<List<MaintainanceRecordDto>> getMaintainanceRecordByStaffId(
            @PathVariable("staffId") String staffId) {
        List<MaintainanceRecordDto> recordDtos = maintenanceRecordService.getMaintainanceRecordByStaff_id(staffId);

        if (recordDtos != null) {
            return ResponseEntity.ok(recordDtos);
        } else {
            return ResponseEntity.notFound().build();
        }
        //Da xong
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('manager')")
    @Transactional
    public ResponseEntity<List<MaintainanceRecordDto>> getAllMaintenanceRecords() {
        List<ServiceAppointment> allAppointments = serviceAppointmentService.findAll();
        List<MaintenanceRecord> records = maintenanceRecordService.getAll(allAppointments);
        List<MaintainanceRecordDto> dtos = maintainanceRecordMapper.toDTOList(records);
        return ResponseEntity.ok(dtos);
        //chua test
    }

    @GetMapping("/all/serviceCenter/{centerId}")
    @PreAuthorize("hasAnyAuthority( 'manager')")
    @Transactional
    public ResponseEntity<List<MaintainanceRecordDto>> getAllMaintenanceRecordsByCenterId(@PathVariable Integer centerId) {
        List<ServiceAppointment> allAppointments = serviceAppointmentService.findAllByServiceCenterId(centerId);
        List<MaintenanceRecord> records = maintenanceRecordService.getAll(allAppointments);
        List<MaintainanceRecordDto> dtos = maintainanceRecordMapper.toDTOList(records);
        return ResponseEntity.ok(dtos);
        //chua test
    }

//    @GetMapping("/all/serviceCenter")
//    @PreAuthorize("hasAnyAuthority('staff', 'manager','technician')")
//    @Transactional
//    public ResponseEntity<List<MaintenanceRecord>> getAllMaintenanceRecordsByCenterId(Authentication authentication) {
//        String username = authentication.getName();
//        User user = userRepository.findByEmail(username).orElse(null);
//        List<ServiceAppointment> allAppointments = serviceAppointmentService.findAllByServiceCenter(user.getServiceCenter()); // you need to have this method
//        List<MaintenanceRecord> records = maintenanceRecordService.getAll(allAppointments);
//        return ResponseEntity.ok(records);
//        //chua test
//    }



}
