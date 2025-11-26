package com.example.Ev.System.service;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.Part;
import com.example.Ev.System.entity.PartUsage;
import com.example.Ev.System.entity.ServiceCenter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PartUsageServiceI {
    public void usePart(Integer partId, Integer quantityUsed, Integer centerId, Integer RecordId);
    public List<Map.Entry<String, Long>> getTop5PartsUsedInLastMonth();
    List<PartUsage> returnUsedParts(Integer appointmentId);

}
