package com.example.Ev.System.controller;

import com.example.Ev.System.dto.ServiceTypeDto;
import com.example.Ev.System.dto.ServiceTypeUpdateDto;
import com.example.Ev.System.service.ServiceTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController

@RequestMapping("/api/service-types")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('customer', 'admin')")
    public ResponseEntity<List<ServiceTypeDto>> getAllServiceTypes() {
        return ResponseEntity.ok(serviceTypeService.getAllDtos());
    }


    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public ResponseEntity<ServiceTypeDto> createServiceType(@Valid @RequestBody ServiceTypeDto dto) {
        ServiceTypeDto created = serviceTypeService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeUpdateDto> updateServiceType(@PathVariable Integer id, @RequestBody @Valid ServiceTypeUpdateDto dto) {
        ServiceTypeUpdateDto updated = serviceTypeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceType(@PathVariable Integer id) {
        serviceTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}