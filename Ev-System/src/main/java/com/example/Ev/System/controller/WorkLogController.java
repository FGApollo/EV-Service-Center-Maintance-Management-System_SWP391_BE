package com.example.Ev.System.controller;

import com.example.Ev.System.dto.WorkLogDto;
import com.example.Ev.System.entity.WorkLog;
import com.example.Ev.System.service.WorkLogService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<WorkLog> >createWorkLog(@RequestBody WorkLogDto dto) {
        List<WorkLog> saved = workLogService.createWorkLog(dto);
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
}
