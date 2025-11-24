package com.example.Ev.System.service;

import com.example.Ev.System.dto.UpdatePartUsage;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
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
    private UserRepository userRepository;

    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    @Autowired
    @Lazy
    private ServiceAppointmentService serviceAppointmentService;

    @Value("${app.notifications.from:no-reply@example.com}")
    private String centerEmail;


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

    @Transactional
    public void usePathNoUsage(Integer partId, Integer quantityUsed, Integer centerId){
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));
        Inventory inventory = inventoryRepository.findByCenterIdAndPart(centerId, part)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for part: " + part.getName()));

        if (inventory.getQuantity() < quantityUsed) {
            throw new RuntimeException("Insufficient stock for part: " + part.getName());
        }
        inventory.setQuantity(inventory.getQuantity() - quantityUsed);
        inventoryRepository.save(inventory);

        if (inventory.getQuantity() < part.getMinStockLevel()) {
            sendStockNotification(part, inventory);
        }
    }

    @Transactional
    public void updatePartUsage(UpdatePartUsage dto, Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Integer centerId = currentUser.getServiceCenter().getId();

        if (!centerId.equals(dto.getCenterId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your center");
        }

        ServiceAppointment serviceAppointment = serviceAppointmentRepository.findById(dto.getAppointmentId());

        if (!serviceAppointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Record not in your center");
        }

        MaintenanceRecord record = maintenanceRecordRepository
                .findFirstByAppointment_IdOrderByIdDesc(serviceAppointment.getId())
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));

        PartUsage usage = record.getPartUsages().stream()
                .filter(u -> u.getPart().getId().equals(dto.getPartId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Part usage not found for part: " + dto.getPartId()));

        Part part = usage.getPart();
        Inventory inventory = inventoryRepository.findByCenterIdAndPart(centerId, part)
                .orElseThrow(() -> new RuntimeException("Inventory not found for part: " + part.getId()));

        int qty = dto.getQuantityUsed();
        int oldQty = usage.getQuantityUsed();

        if (dto.getStatus() == 0) {
            if (oldQty < qty)
                throw new RuntimeException("Cannot reduce usage more than previously recorded.");
            usage.setQuantityUsed(oldQty - qty);
            inventory.setQuantity(inventory.getQuantity() + qty);
        } else {
            if (inventory.getQuantity() < qty)
                throw new RuntimeException("Not enough inventory to increase usage.");
            usage.setQuantityUsed(oldQty + qty);
            inventory.setQuantity(inventory.getQuantity() - qty);
        }

        inventory.setLastUpdated(LocalDateTime.now());

        partUsageRepository.save(usage);
        inventoryRepository.save(inventory);
    }



    @Override
    public List<Map.Entry<String, Long>> getTop5PartsUsedInLastMonth() {
        Instant startTime = Instant.now();
        Instant endTime = startTime.minus(30, ChronoUnit.DAYS);
        List<PartUsage> partUsages = partUsageRepository.findByRecord_StartTimeBetween(endTime, startTime);

        if (partUsages.isEmpty()) {
            throw new NotFoundException("No parts usage in last month");
        }

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


    public void sendStockNotification(Part part, Inventory inventory) {
        // Implement notification logic (e.g., email, SMS)
        System.out.println("Notification: Stock for part " + part.getName() +
                " | Remaining: " + inventory.getQuantity());

        String subject = "Low Stock Alert";
        String text = "Part " + inventory.getPart().getName()
                + " has reached minimum stock level. Current: "
                + inventory.getQuantity();
        List<User> allManager = userRepository.findAllByRoleAndServiceCenter("manager", inventory.getCenter());
        for (User user : allManager) {
            sendNotification(user.getEmail(), subject, text);
        }
//        sendNotification("thanhbinh642842@gmail.com", subject, text);
    }

    public void sendNotification(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(centerEmail); // must be same as spring.mail.username
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
