package com.example.Ev.System.controller;

import com.example.Ev.System.dto.PartUsageRequest;
import com.example.Ev.System.dto.UpdatePartUsage;
import com.example.Ev.System.service.PartService;
import com.example.Ev.System.service.PartUsageService;
import com.example.Ev.System.service.PartUsageServiceI;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PartUsageController {
    @Autowired
    private PartUsageServiceI partUsageServiceI;
    @Autowired
    private PartUsageService partUsageService;

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

    @PostMapping("/return-parts/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('staff', 'technician')")
    public String returnUnusedParts(@PathVariable Integer appointmentId) {
        partUsageService.returnUsedParts(appointmentId);
        return "Unused parts returned successfully";
    }
}
