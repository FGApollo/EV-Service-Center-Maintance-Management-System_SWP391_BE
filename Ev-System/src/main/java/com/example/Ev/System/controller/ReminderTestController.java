package com.example.Ev.System.controller;

import com.example.Ev.System.service.MaintenanceReminderScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReminderTestController {

    private final MaintenanceReminderScheduler scheduler;

    public ReminderTestController(MaintenanceReminderScheduler scheduler) {
        this.scheduler = scheduler;
    }

    // API test thủ công (gọi tay)
    @GetMapping("/api/auth/reminder/run")
    public String runSchedulerManually() {
        scheduler.processDueReminders();
        return "✅ Reminder job executed manually!";
    }
}
