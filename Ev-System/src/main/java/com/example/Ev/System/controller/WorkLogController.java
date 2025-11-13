package com.example.Ev.System.controller;

import com.example.Ev.System.dto.WorkLogDto;
import com.example.Ev.System.entity.Worklog;
import com.example.Ev.System.service.WorkLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/worklogs")
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @PostMapping
    public ResponseEntity<List<Worklog> >createWorkLog(@RequestBody WorkLogDto dto) {
        List<Worklog> saved = workLogService.createWorkLog(dto);
        return ResponseEntity.ok(saved);
        //Da xong
        //TODO : WorkLog se dc auto luu khi xong 1 appointment , hoi may thg kia hd nhu the nao

    }

    @PostMapping("/{id}")
    public ResponseEntity<List<WorkLogDto> >createAutoWorkLog( @PathVariable Integer id) {
        List<WorkLogDto> saved = workLogService.autoCreateWorkLog(id);
        return ResponseEntity.ok(saved);
        //Da xong
        //TODO : WorkLog se dc auto luu khi xong 1 appointment , hoi may thg kia hd nhu the nao

    }

    @GetMapping("/center")
    @PreAuthorize("hasAnyAuthority('manager')")
    public ResponseEntity<List<WorkLogDto>> getAllWorkLogsByCenter(Authentication authentication) {
        List<WorkLogDto> workLogs = workLogService.getAllWorkLogsByCenterId(authentication);
        return ResponseEntity.ok(workLogs);
    }

    @GetMapping("/center/{centerId}")
    @PreAuthorize("hasAnyAuthority('manager')")
    public ResponseEntity<List<WorkLogDto>> getAllWorkLogsByCenter(@RequestParam int centerId) {
        List<WorkLogDto> workLogs = workLogService.getWorkLogsByCenterId(centerId);
        return ResponseEntity.ok(workLogs);
    }

}
