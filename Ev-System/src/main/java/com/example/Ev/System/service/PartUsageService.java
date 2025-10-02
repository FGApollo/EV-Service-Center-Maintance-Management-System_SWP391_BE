package com.example.Ev.System.service;

import com.example.Ev.System.dto.PartUsageRequest;
import com.example.Ev.System.entity.InventoryEntity;
import com.example.Ev.System.entity.PartEntity;
import com.example.Ev.System.entity.PartUsageEntity;
import com.example.Ev.System.repository.InventoryRepository;
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
    private EmailService emailService;

    @Transactional
    public void usePart(Integer partId, Integer quantityUsed) {
        PartEntity partEntity = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));
        InventoryEntity inventory = inventoryRepository.findByPart(partEntity)
                .orElseThrow(() -> new RuntimeException("Inventory not found for part"));

        if (inventory.getQuantity() < quantityUsed) {
            throw new RuntimeException("Insufficient stock for part: " + partEntity.getName());
        }
        inventory.setQuantity(inventory.getQuantity() - quantityUsed);
        inventoryRepository.save(inventory);

        //save used part record
        PartUsageEntity partUsageEntity = new PartUsageEntity();
        partUsageEntity.setPart(partEntity);
        partUsageEntity.setQuantityUsed(quantityUsed);
        partUsageEntity.setUnitCost(partEntity.getUnitPrice());
        partUsageRepository.save(partUsageEntity);

        // send notification if stock below minimum level
        if (inventory.getQuantity() < partEntity.getMinStockLevel()) {
            sendStockNotification(partEntity, inventory);
        }
    }

    public void sendStockNotification(PartEntity partEntity, InventoryEntity inventoryEntity) {
        // Implement notification logic (e.g., email, SMS)
//        System.out.println("Notification: Stock for part " + partEntity.getName() +
//                " | Remaining: " + inventoryEntity.getQuantity());
        String subject = "Low Stock Alert";
        String text = "Part " + partEntity.getName()
                + " has reached minimum stock level. Current: "
                + inventoryEntity.getQuantity();

        emailService.sendMail("manager@example.com", subject, text);
    }
}
