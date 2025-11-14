package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaintainanceRecordDto {
    private String vehicleCondition;
    private String checklist;
    private String remarks;
    private List<PartUsageDto> partsUsed;
    private List<Integer> staffIds;
    private Instant startTime;
    private Instant endTime;
}
