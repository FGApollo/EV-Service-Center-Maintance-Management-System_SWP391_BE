package com.example.Ev.System.service;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.Part;
import com.example.Ev.System.repository.InventoryRepository;
import com.example.Ev.System.repository.PartRepository;
import com.example.Ev.System.repository.ServiceCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartService implements PartServiceI{
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ServiceCenterRepository serviceCenterRepository;

    @Override
    public List<Part> getAll() {
        return partRepository.findAll();
    }

    @Override
    public Part getById(Integer id) {
        return partRepository.findById(id).get();
    }

    @Override
    public Part createPart(Integer centerId, Part part) {
        part.setCreatedAt(Instant.now());

        Part savedPart = partRepository.save(part);

        Inventory inventory = new Inventory();
        inventory.setPart(part);
        inventory.setCenter(serviceCenterRepository.findById(centerId).get());
        inventory.setQuantity(part.getMinStockLevel());
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);
        return savedPart;
    }

    @Override
    public Part updatePart(Integer id, Part part) {
        return partRepository.findById(id)
                .map(existingPart -> {
                    existingPart.setName(part.getName());
                    existingPart.setDescription(part.getDescription());
                    existingPart.setUnitPrice(part.getUnitPrice());
                    existingPart.setMinStockLevel(part.getMinStockLevel());
            return partRepository.save(existingPart);
        })
                .orElseThrow(() -> new RuntimeException("Part not found with id " + id));
    }

    @Override
    public void deletePart(Integer id) {
        if(!partRepository.existsById(id)) {
            throw new RuntimeException("Part not found with id " + id);
        }
        partRepository.deleteById(id);
    }
}
