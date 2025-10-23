package com.example.Ev.System.service;

import com.example.Ev.System.entity.MaintenanceReminder;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
import com.example.Ev.System.repository.MaintenanceReminderRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final MaintenanceReminderRepository reminderRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;

    @Value("${app.notifications.from:no-reply@example.com}")
    private String from;

    public NotificationService(JavaMailSender mailSender,
                               MaintenanceReminderRepository reminderRepo,
                               VehicleRepository vehicleRepo,
                               UserRepository userRepo) {
        this.mailSender = mailSender;
        this.reminderRepo = reminderRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    public void sendReminderEmail(MaintenanceReminder reminder) {
        String to = fetchEmailByVehicleId(Long.valueOf(reminder.getVehicle().getId()));
        if (to == null) {
            System.err.println("Không tìm thấy email cho vehicle_id=" + reminder.getVehicle().getId());
            reminder.setIsSent(true);
            reminder.setSentAt(Instant.from(LocalDateTime.now()));
            reminderRepo.save(reminder);
            return;
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Thông báo bảo dưỡng định kỳ");
        String body = (reminder.getMessage() != null)
                ? reminder.getMessage()
                : "Xe của bạn đã đến kỳ bảo dưỡng định kỳ. Vui lòng đặt lịch hẹn tại trung tâm dịch vụ gần nhất.";
        msg.setText(body);

        mailSender.send(msg);

        reminder.setIsSent(true);
        reminder.setSentAt(Instant.now());
        reminderRepo.save(reminder);

        System.out.println("✅ Gửi email nhắc bảo dưỡng cho: " + to);
    }

    private String fetchEmailByVehicleId(Long vehicleId) {
        if (vehicleId == null) return null;

        // Lấy vehicle theo vehicle_id
        Vehicle vehicle = vehicleRepo.findVehicleById(vehicleId.intValue());
        if (vehicle == null) return null;

        // Lấy user (chủ xe) theo customer_id
        Integer customerId = vehicle.getCustomer().getId();
        if (customerId == null) return null;

        Optional<User> userOpt = userRepo.findById(customerId);
        return userOpt.map(User::getEmail).orElse(null);
    }
}
