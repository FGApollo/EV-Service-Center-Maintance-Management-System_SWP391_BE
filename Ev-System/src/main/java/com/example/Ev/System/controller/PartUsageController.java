package com.example.Ev.System.controller;

import com.example.Ev.System.dto.PartUsageRequest;
import com.example.Ev.System.repository.PartUsageRepository;
import com.example.Ev.System.service.PartUsageService;
import com.example.Ev.System.service.PartUsageServiceI;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/technician")
@AllArgsConstructor
public class PartUsageController {
    @Autowired
    private PartUsageServiceI partUsageServiceI;

    @PostMapping("/part_usage")
    public String usePart(@RequestBody PartUsageRequest partUsageRequest) {
        partUsageServiceI.usePart(partUsageRequest.getPartId(),
                partUsageRequest.getQuantityUsed(),
                partUsageRequest.getCenterId(),
                partUsageRequest.getRecordId());
        return "Part usage recorded successfully";
    }

}
