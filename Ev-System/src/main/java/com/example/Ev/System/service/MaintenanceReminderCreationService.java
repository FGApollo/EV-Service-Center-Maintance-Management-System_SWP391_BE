package com.example.Ev.System.service;

import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.MaintenanceReminder;
import com.example.Ev.System.repository.MaintenanceRecordRepository;
import com.example.Ev.System.repository.MaintenanceReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;

@Service
public class MaintenanceReminderCreationService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final MaintenanceReminderRepository maintenanceReminderRepository;

    public MaintenanceReminderCreationService(MaintenanceRecordRepository maintenanceRecordRepository,
                                              MaintenanceReminderRepository maintenanceReminderRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.maintenanceReminderRepository = maintenanceReminderRepository;
    }

    @Transactional
    public void createReminderForAppointmentIfDone(Integer appointmentId) {
        // Lấy MaintenanceRecord mới nhất của appointment
        MaintenanceRecord record = maintenanceRecordRepository
                .findFirstByAppointment_IdOrderByIdDesc(appointmentId)
                .orElse(null);
        if (record == null || record.getEndTime() == null || record.getAppointment() == null || record.getAppointment().getVehicle() == null) {
            return; // Khong đủ dữ liệu để tạo reminder
        }

        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        Instant reminderAt = record.getEndTime().atZone(zone).plusMonths(6).toInstant();

        Long vehicleId = Long.valueOf(record.getAppointment().getVehicle().getId());
        boolean exists = maintenanceReminderRepository.existsByVehicleIdAndReminderDate(vehicleId, reminderAt);
        if (exists) {
            return; // Đã có reminder trùng
        }

        MaintenanceReminder reminder = new MaintenanceReminder();
        reminder.setVehicle(record.getAppointment().getVehicle());
        reminder.setReminderDate(reminderAt);
        reminder.setMessage("Nhắc bảo dưỡng định kỳ (sau 6 tháng kể từ lần bảo dưỡng gần nhất)");
        reminder.setIsSent(false);
        maintenanceReminderRepository.save(reminder);
    }
}