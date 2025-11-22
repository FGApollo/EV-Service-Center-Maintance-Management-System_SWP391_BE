package com.example.Ev.System.service;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    public InventoryRepository inventoryRepository;
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }


}
