package com.example.Ev.System.controller;

import com.example.Ev.System.dto.ServiceTypeDto;
import com.example.Ev.System.service.ServiceTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@PreAuthorize("hasAuthority('customer')")
@RequestMapping("/api/service-types")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceTypeDto>> getAllServiceTypes() {
        return ResponseEntity.ok(serviceTypeService.getAllDtos());
    }
}