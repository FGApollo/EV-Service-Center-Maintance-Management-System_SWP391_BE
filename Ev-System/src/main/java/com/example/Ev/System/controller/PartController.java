package com.example.Ev.System.controller;

import com.example.Ev.System.dto.PartDto;
import com.example.Ev.System.entity.Part;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.service.PartServiceI;
import com.example.Ev.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management2/parts")
public class PartController {
    @Autowired
    private PartServiceI partService;
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin')")
    public ResponseEntity<List<PartDto>> getAllParts() {
        List<PartDto> parts = partService.getAll();
        return ResponseEntity.ok(parts);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<Part> getPartById(@PathVariable Integer id) {
        Part part = partService.getById(id);
        return ResponseEntity.ok(part);
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<Part> createPart(@RequestBody Part part, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        Part createdPart = partService.createPart(centerId, part);
        return ResponseEntity.ok(createdPart);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<Part> updatePart(@PathVariable Integer id, @RequestBody Part part) {
        Part updatedPart = partService.updatePart(id, part);
        return ResponseEntity.ok(updatedPart);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<Void> deletePart(@PathVariable Integer id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
