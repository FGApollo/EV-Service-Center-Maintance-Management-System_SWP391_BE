package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class MaintainanceRecordDto {
    String vehicleCondition;
    String checklist;
    String remarks;
    Map<Integer, Integer> partsUsed;
}
