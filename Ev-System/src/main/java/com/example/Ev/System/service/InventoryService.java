package com.example.Ev.System.service;

import com.example.Ev.System.dto.PartQuantityDto;
import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.Part;
import com.example.Ev.System.repository.InventoryRepository;
import com.example.Ev.System.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public List<PartQuantityDto> getPartQuantitiesByCenter(Integer centerId) {
        List<Inventory> inventories = inventoryRepository.findByCenterId(centerId);

        return inventories.stream()
                .map(inv -> new PartQuantityDto(
                        inv.getPart().getId(),
                        inv.getPart().getName(),
                        inv.getQuantity()
                ))
                .toList();
    }

}
