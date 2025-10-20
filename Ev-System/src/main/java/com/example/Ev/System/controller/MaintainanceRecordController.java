package com.example.Ev.System.controller;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.mapper.MaintainanceRecordMapper;
import com.example.Ev.System.service.MaintenanceRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/MaintainanceRecord")
public class MaintainanceRecordController {
    private final MaintenanceRecordService maintenanceRecordService;

    public MaintainanceRecordController(MaintenanceRecordService maintenanceRecordService) {
        this.maintenanceRecordService = maintenanceRecordService;
    }

    @PostMapping("/{appointmentId}")
    public ResponseEntity<MaintainanceRecordDto> createMaintenanceRecord(
            @PathVariable("appointmentId") Integer appointmentId,
            @RequestBody MaintainanceRecordDto maintainanceRecordDto) {
        if(maintenanceRecordService.findMaintainanceRecordByAppointmentId(appointmentId)){
            maintenanceRecordService.updateMaintainanceRecord(appointmentId, maintainanceRecordDto);
        }
        else {
            maintenanceRecordService.recordMaintenance(appointmentId, maintainanceRecordDto);
        }
        return ResponseEntity.ok(maintainanceRecordDto);
        //Da xong
    }

    @GetMapping("/staff/{staffId}")
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


}
