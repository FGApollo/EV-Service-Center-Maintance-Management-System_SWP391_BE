package com.example.Ev.System.service;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.Part;
import com.example.Ev.System.repository.InventoryRepository;
import com.example.Ev.System.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService implements InventoryServiceI {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PartRepository partRepository;

    @Override
    @Transactional
    public void addQuantityToInventory(Integer centerId, Integer partId, Integer quantity) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        Inventory inventory = inventoryRepository.findByCenterIdAndPart(centerId, part)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }
}
