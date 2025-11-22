package com.example.Ev.System.controller;

import com.example.Ev.System.entity.Part;
import com.example.Ev.System.service.PartServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management/parts")

public class PartController {
    @Autowired
    private PartServiceI partService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('technician')")
    public ResponseEntity<List<Part>> getAllParts() {
        List<Part> parts = partService.getAll();
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('technician')")
    public ResponseEntity<Part> getPartById(@PathVariable Integer id) {
        Part part = partService.getById(id);
        return ResponseEntity.ok(part);
    }

    @PostMapping("/create")
    public ResponseEntity<Part> createPart(@RequestBody Part part) {
        Part createdPart = partService.createPart(part);
        return ResponseEntity.ok(createdPart);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Part> updatePart(@PathVariable Integer id, @RequestBody Part part) {
        Part updatedPart = partService.updatePart(id, part);
        return ResponseEntity.ok(updatedPart);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Integer id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
