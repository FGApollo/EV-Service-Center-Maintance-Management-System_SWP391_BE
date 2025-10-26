package com.example.Ev.System.service;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationProgressService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@ev-service.local}")
    private String from;

    public NotificationProgressService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Transactional
    public void sendAppointmentStatusChanged(@NonNull User customer,
                                             @NonNull ServiceAppointment appointment,
                                             String oldStatus,
                                             String newStatus) {
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            return; // Không có email để gửi
        }

        String subject = String.format("Cập nhật trạng thái lịch hẹn #%d: %s → %s",
                appointment.getId(),
                safe(oldStatus),
                safe(newStatus));

        String body = buildBody(customer, appointment, oldStatus, newStatus);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(customer.getEmail());
        msg.setSubject(subject);
        msg.setText(body);

        try {
            mailSender.send(msg);
        } catch (Exception ex) {
            // Có thể thay bằng logger nếu dự án đã dùng logging
            System.err.println("Gửi email thất bại: " + ex.getMessage());
        }
    }


    private String buildBody(User customer,
                             ServiceAppointment appointment,
                             String oldStatus,
                             String newStatus) {
        String serviceCenterName = appointment.getServiceCenter() != null ? appointment.getServiceCenter().getName() : "Trung tâm dịch vụ";
        String vehicleModel = appointment.getVehicle() != null ? appointment.getVehicle().getModel() : "Xe của bạn";

        return "Xin chào " + safe(customer.getFullName() != null ? customer.getFullName() : customer.getEmail()) + ",\n\n"
                + "Trạng thái lịch hẹn của bạn đã được cập nhật.\n"
                + "- Mã lịch hẹn: #" + appointment.getId() + "\n"
                + "- Trung tâm: " + serviceCenterName + "\n"
                + "- Phương tiện: " + vehicleModel + "\n"
                + "- Ngày hẹn: " + (appointment.getAppointmentDate() != null ? appointment.getAppointmentDate() : "Chưa xác định") + "\n"
                + "- Trạng thái: " + safe(oldStatus) + " → " + safe(newStatus) + "\n\n"
                + "Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ bộ phận hỗ trợ.\n"
                + "Trân trọng,\nEV Service Center";
    }

    private String safe(String s) {
        return s == null ? "N/A" : s;
    }
}