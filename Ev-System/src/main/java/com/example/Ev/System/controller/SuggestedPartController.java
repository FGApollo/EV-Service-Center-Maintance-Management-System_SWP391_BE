package com.example.Ev.System.controller;

import com.example.Ev.System.dto.SuggestedPartDto;
import com.example.Ev.System.entity.SuggestedPart;
import com.example.Ev.System.service.SuggestedPartServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suggested-part")
public class SuggestedPartController {
    @Autowired
    private SuggestedPartServiceI suggestedPartService;

    @PreAuthorize("hasAnyAuthority('staff', 'customer')")
    @GetMapping("/{appointmentId}")
    public ResponseEntity<List<SuggestedPartDto>> getAllSuggestedPartByAppointmentId(@PathVariable Integer appointmentId) {
        return ResponseEntity.ok(suggestedPartService.getAllSuggestedPartsByAppointmentId(appointmentId));
    }

    @GetMapping("/one/{id}")
    public SuggestedPart getSuggestedPartById(@PathVariable Integer id) {
        return suggestedPartService.getSuggestedPartById(id);
    }
}
