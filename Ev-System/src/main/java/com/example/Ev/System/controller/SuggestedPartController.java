package com.example.Ev.System.controller;

import com.example.Ev.System.dto.CenterDTO;
import com.example.Ev.System.dto.SuggestPartDto;
import com.example.Ev.System.service.SuggestedPartService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suggested_part")
public class SuggestedPartController {
    private final SuggestedPartService suggestedPartService;

    public SuggestedPartController(SuggestedPartService suggestedPartService) {
        this.suggestedPartService = suggestedPartService;
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('customer', 'staff')")
    public ResponseEntity<List<SuggestPartDto>>
    getAllSuggestedPartForAppointment(@PathVariable @Positive(message = "id phải > 0") Integer appointmentId){

        List<SuggestPartDto> suggestPartDtos = suggestedPartService.getAllSuggestPartForAppointment(appointmentId);

        return ResponseEntity.ok(suggestPartDtos);
    }

}
