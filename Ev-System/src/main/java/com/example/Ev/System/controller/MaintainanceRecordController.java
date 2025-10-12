package com.example.Ev.System.controller;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.mapper.MaintainanceRecordMapper;
import com.example.Ev.System.service.MaintenanceRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        maintenanceRecordService.recordMaintenance(appointmentId, maintainanceRecordDto);
        return ResponseEntity.ok(maintainanceRecordDto);
        //Da xong
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<MaintainanceRecordDto> getMaintainanceRecordByStaffId(
            @PathVariable("staffId") Integer staffId) {
        MaintainanceRecordDto recordDto = maintenanceRecordService.getMaintainanceRecordByStaff_id(staffId);

        if (recordDto != null) {
            return ResponseEntity.ok(recordDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
