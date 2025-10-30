package com.example.Ev.System.service;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.Part;
import org.springframework.stereotype.Service;

@Service
public interface PartUsageServiceI {
    public void usePart(Integer partId, Integer quantityUsed, Integer centerId, Integer RecordId);
}
