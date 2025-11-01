package com.example.Ev.System.service;

import com.example.Ev.System.entity.*;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private ServiceCenterRepository serviceCenterRepository;

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

    @Override
    public List<Map.Entry<String, Long>> getTop5PartsUsedInLastMonth() {
        Instant startTime = Instant.now();
        Instant endTime = startTime.minus(30, ChronoUnit.DAYS);
        List<PartUsage> partUsages = partUsageRepository.findByRecord_StartTimeBetween(endTime, startTime);

        Map<String, Long> partUsageCount = partUsages.stream()
                .collect(Collectors.groupingBy(
                        pu -> pu.getPart().getName(),
                        Collectors.summingLong(PartUsage::getQuantityUsed)
                ));
        return partUsageCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .toList();
    }



//    @Override
//    public Map<ServiceCenter, List<PartUsage>> top5UsedPartsPerCenter() {
////        List<Inventory> inventories = inventoryRepository.findAll();
////        Map<ServiceCenter, List<PartUsage>> topParts = new HashMap<>();
////        for (Inventory inventory : inventories) {
////            List<PartUsage> top5 = inventory.getCenter().getPartUsages().stream()
////                    .filter(pu -> pu.getPart().equals(inventory.getPart()))
////                    .sorted((pu1, pu2) -> Integer.compare(pu2.getQuantityUsed(), pu1.getQuantityUsed()))
////                    .limit(5)
////                    .toList();
////            topParts.put(inventory.getCenter(), top5);
////        }
//        return Map.of();
//}

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
