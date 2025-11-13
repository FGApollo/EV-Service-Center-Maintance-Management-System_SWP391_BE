package com.example.Ev.System.controller;

import com.example.Ev.System.dto.CenterDTO;
import com.example.Ev.System.service.CenterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.Ev.System.validation.ValidationGroups.Create;
import com.example.Ev.System.validation.ValidationGroups.Update;

import java.util.List;

@RestController
@RequestMapping("/api/center")
@PreAuthorize("hasAuthority('admin')")
@Validated
public class ServiceCenterController {

    private final CenterService centerService;

    public ServiceCenterController(CenterService centerService) {
        this.centerService = centerService;
    }


    @GetMapping
    public ResponseEntity<List<CenterDTO>> getAllCenters() {
        List<CenterDTO> centers = centerService.GetAllCenter();
        return ResponseEntity.ok(centers);
    }


    @PostMapping
    public ResponseEntity<CenterDTO> createCenter(@RequestBody @Validated(Create.class) CenterDTO centerDTO) {
        CenterDTO createdCenter = centerService.CreateCenter(centerDTO);
        return ResponseEntity.ok(createdCenter);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CenterDTO> updateCenter(@PathVariable @Positive(message = "id phải > 0") Integer id,
                                                  @RequestBody @Validated(Update.class) CenterDTO centerDTO) {
        CenterDTO updatedCenter = centerService.UpdateCenter(centerDTO, id);
        return ResponseEntity.ok(updatedCenter);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCenter(@PathVariable Integer id) {
        centerService.DeleteCenter(id);
        return ResponseEntity.ok("Center with id " + id + " has been deactivated successfully.");
    }
}
