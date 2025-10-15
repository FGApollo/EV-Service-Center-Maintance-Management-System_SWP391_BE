package com.example.Ev.System.controller;

import com.example.Ev.System.dto.PartUsageRequest;
import com.example.Ev.System.repository.PartUsageRepository;
import com.example.Ev.System.service.PartUsageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class PartUsageController {
    @Autowired
    private PartUsageService partUsageService;

    @PutMapping("/part_usage")
    public String usePart(@RequestBody PartUsageRequest partUsageRequest) {
        partUsageService.usePart(partUsageRequest.getPartId(),
                partUsageRequest.getQuantityUsed(),
                partUsageRequest.getCenterId(),
                partUsageRequest.getRecordId());
        return "Part usage recorded successfully";
    }
}
