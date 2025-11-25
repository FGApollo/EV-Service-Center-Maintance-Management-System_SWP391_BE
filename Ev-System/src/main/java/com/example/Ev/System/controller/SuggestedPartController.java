package com.example.Ev.System.controller;

import com.example.Ev.System.dto.RequestSuggestPart;
import com.example.Ev.System.dto.SuggestPartDto;
import com.example.Ev.System.service.SuggestedPartService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyAuthority('customer', 'staff')")
    public ResponseEntity<SuggestPartDto> acceptSuggestedPart(@PathVariable @Positive Integer id){
        SuggestPartDto dto = suggestedPartService.acceptSuggestedPart(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/deny")
    @PreAuthorize("hasAnyAuthority('customer', 'staff')")
    public ResponseEntity<SuggestPartDto> denySuggestedPart(@PathVariable @Positive Integer id){
        SuggestPartDto dto = suggestedPartService.denySuggestedPart(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('technician')")
    public ResponseEntity<List<RequestSuggestPart>> createSuggestParts(
            @RequestBody List<RequestSuggestPart> requestSuggestParts) {
        List<RequestSuggestPart> createdParts = suggestedPartService.createSuggestParts(requestSuggestParts);
        return ResponseEntity.ok(createdParts);

    }

}
