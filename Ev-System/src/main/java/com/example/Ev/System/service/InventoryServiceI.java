package com.example.Ev.System.service;

import com.example.Ev.System.dto.PartQuantityDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryServiceI {
    public void addQuantityToInventory(Integer centerId, Integer partId, Integer quantity);
    List<PartQuantityDto> getPartQuantitiesByCenter(Integer centerId);
}
