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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class PartUsageService implements PartUsageServiceI{
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private PartUsageRepository partUsageRepository;
    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;
    @Autowired
    private JavaMailSender mailSender;

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

        String subject = "Low Stock Alert";
        String text = "Part " + inventory.getPart().getName()
                + " has reached minimum stock level. Current: "
                + inventory.getQuantity();

        sendNotification("manager@example.com", subject, text);
    }

    public void sendNotification(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("thanhbinh642842@gmail.com"); // must be same as spring.mail.username
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
