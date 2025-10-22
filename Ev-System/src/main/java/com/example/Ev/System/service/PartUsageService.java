package com.example.Ev.System.service;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.Part;
import com.example.Ev.System.entity.PartUsage;
import com.example.Ev.System.repository.InventoryRepository;
import com.example.Ev.System.repository.MaintenanceRecordRepository;
import com.example.Ev.System.repository.PartRepository;
import com.example.Ev.System.repository.PartUsageRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class PartUsageService {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private PartUsageRepository partUsageRepository;
    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Transactional
    public void usePart(Integer partId, Integer quantityUsed, Integer centerId, Integer RecordId) {
        MaintenanceRecord record = maintenanceRecordRepository.findById(RecordId)
                .orElseThrow(() -> new RuntimeException("Maintenance Record not found"));
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));
        Inventory inventory = inventoryRepository.findByCenterIdAndPart(centerId, part)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for part: " + part.getName()));


        if (inventory.getQuantity() < quantityUsed) {
            throw new RuntimeException("Insufficient stock for part: " + part.getName());
        }
        inventory.setQuantity(inventory.getQuantity() - quantityUsed);
        inventoryRepository.save(inventory);

        //save used part record
        PartUsage partUsageEntity = new PartUsage();
        partUsageEntity.setRecord(record);
        partUsageEntity.setPart(part);
        partUsageEntity.setQuantityUsed(quantityUsed);
        partUsageEntity.setUnitCost(part.getUnitPrice());
        partUsageRepository.save(partUsageEntity);

        // send notification if stock below minimum level
        if (inventory.getQuantity() < part.getMinStockLevel()) {
            sendStockNotification(part, inventory);
        }
    }

    public void sendStockNotification(Part part, Inventory inventory) {
        // Implement notification logic (e.g., email, SMS)
        System.out.println("Notification: Stock for part " + part.getName() +
                " | Remaining: " + inventory.getQuantity());
    }
}
