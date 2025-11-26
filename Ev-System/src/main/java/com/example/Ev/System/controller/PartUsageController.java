package com.example.Ev.System.controller;

import com.example.Ev.System.dto.PartUsageDto;
import com.example.Ev.System.dto.PartUsageRequest;
import com.example.Ev.System.dto.UpdatePartUsage;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PartUsageController {
    @Autowired
    private PartUsageServiceI partUsageServiceI;
    @Autowired
    private PartUsageService partUsageService;
    @Autowired
    private MaintenanceRecordService maintenanceRecordService;
    @Autowired
    private ServiceAppointmentService serviceAppointmentService;

    @PostMapping("/part_usage")
    public String usePart(@RequestBody PartUsageRequest partUsageRequest) {
        partUsageServiceI.usePart(partUsageRequest.getPartId(),
                partUsageRequest.getQuantityUsed(),
                partUsageRequest.getCenterId(),
                partUsageRequest.getRecordId());
        return "Part usage recorded successfully";
    }

    @PutMapping("/technician/part_usage/update")
    @PreAuthorize("hasAnyAuthority('technician')")
    public void updatePartUsage(@RequestBody UpdatePartUsage updatePartUsage, Authentication authentication) {
        partUsageService.updatePartUsage(updatePartUsage,authentication);
    } // can test // da test xong

<<<<<<< HEAD
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('technician')")
    public ResponseEntity<List<PartUsageDto>> getPartUsageByAppointmentId(@PathVariable int id) {
        ServiceAppointment appointment = serviceAppointmentService.findById(id);
        return ResponseEntity.ok(maintenanceRecordService.getPartUsageByAppointmentId(appointment));
    }

=======
    @PostMapping("/return-parts/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('staff', 'technician')")
    public String returnUnusedParts(@PathVariable Integer appointmentId) {
        partUsageService.returnUsedParts(appointmentId);
        return "Unused parts returned successfully";
    }
>>>>>>> origin/main
}
