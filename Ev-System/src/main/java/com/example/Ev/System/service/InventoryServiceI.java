package com.example.Ev.System.service;

import org.springframework.stereotype.Service;

@Service
public interface InventoryServiceI {
    public void addQuantityToInventory(Integer centerId, Integer partId, Integer quantity);
}
