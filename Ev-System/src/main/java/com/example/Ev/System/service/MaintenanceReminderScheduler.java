package com.example.Ev.System.service;

import com.example.Ev.System.entity.MaintenanceReminder;
import com.example.Ev.System.repository.MaintenanceReminderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class MaintenanceReminderScheduler {
    private final MaintenanceReminderRepository reminderRepo;
    private final NotificationService notificationService;

    // Múi giờ Asia/Ho_Chi_Minh
    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public MaintenanceReminderScheduler(MaintenanceReminderRepository reminderRepo,
                                        NotificationService notificationService) {
        this.reminderRepo = reminderRepo;
        this.notificationService = notificationService;
    }


    // chạy hàng ngày lúc 09:00 Asia/Ho_Chi_Minh
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Ho_Chi_Minh")
    public void processDueReminders() {
        Instant now = LocalDateTime.now(ZONE).atZone(ZONE).toInstant();
        List<MaintenanceReminder> due = reminderRepo.findDueReminders(now);

        for (MaintenanceReminder r : due) {
            processSingleReminder(r);
        }
    }

    @Transactional
    public void processSingleReminder(MaintenanceReminder r) {
        try {
            notificationService.sendReminderEmail(r);

            LocalDateTime sentAt = (r.getSentAt() != null)
                    ? LocalDateTime.ofInstant(r.getSentAt(), ZONE)
                    : LocalDateTime.now(ZONE);

            Instant next = sentAt.plusMonths(6).atZone(ZONE).toInstant();

            boolean existsNext = reminderRepo.existsByVehicleIdAndReminderDate(
                    Long.valueOf(r.getVehicle().getId()), next);
            if (!existsNext) {
                MaintenanceReminder nextRem = new MaintenanceReminder();
                nextRem.setVehicle(r.getVehicle());
                nextRem.setReminderDate(next);
                nextRem.setMessage("Nhắc bảo dưỡng định kỳ (chu kỳ 6 tháng)");
                nextRem.setIsSent(false);
                reminderRepo.save(nextRem);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}