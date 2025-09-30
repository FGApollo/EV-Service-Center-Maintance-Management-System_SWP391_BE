package com.example.Ev.System.controller;

import com.example.Ev.System.dto.PartUsageRequest;
import com.example.Ev.System.repository.PartUsageRepository;
import com.example.Ev.System.service.PartUsageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/part_usage")
@AllArgsConstructor
public class PartUsageController {
    @Autowired
    private PartUsageService partUsageService;

    @PostMapping
    public String usePart(@RequestBody PartUsageRequest partUsageRequest) {
        partUsageService.usePart(partUsageRequest.getPartId(), partUsageRequest.getQuantityUsed());
        return "Part usage recorded successfully";
    }
}
