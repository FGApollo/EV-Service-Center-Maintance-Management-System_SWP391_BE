package com.example.Ev.System.service;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.ServiceType;
import com.example.Ev.System.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
            System.err.println("Gửi email thất bại: " + ex.getMessage());
        }
    }

    // MỚI: Email xác nhận đặt lịch thành công
    @Transactional
    public void sendAppointmentBooked(@NonNull User customer,
                                      @NonNull ServiceAppointment appointment, List<ServiceType> serviceTypeList) {
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            return;
        }

        String subject = String.format("Xác nhận đặt lịch thành công – Mã #%d", appointment.getId());
        String body = buildBookedBody(customer, appointment, serviceTypeList);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(customer.getEmail());
        msg.setSubject(subject);
        msg.setText(body);

        try {
            mailSender.send(msg);
        } catch (Exception ex) {
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
                + "Trân trọng,\nEV Service Center";
    }

    private String buildBookedBody(User customer,
                                   ServiceAppointment appointment, List<ServiceType> serviceTypeList) {
        String serviceCenterName = appointment.getServiceCenter() != null ? appointment.getServiceCenter().getName() : "Trung tâm dịch vụ";
        String vehicleModel = appointment.getVehicle() != null ? appointment.getVehicle().getModel() : "Xe của bạn";

        String services = (serviceTypeList == null || serviceTypeList.isEmpty())
                ? "—"
                : serviceTypeList.stream()
                .map(ServiceType::getName)
                .collect(Collectors.joining(", "));

        return "Kính gửi " + safe(customer.getFullName() != null ? customer.getFullName() : customer.getEmail()) + ",\n\n"
                + "EV Service Center trân trọng xác nhận lịch hẹn của Quý khách đã được đặt thành công.\n\n"
                + "Thông tin lịch hẹn:\n"
                + "- Mã lịch hẹn: #" + appointment.getId() + "\n"
                + "- Trung tâm: " + serviceCenterName + "\n"
                + "- Phương tiện: " + vehicleModel + "\n"
                + "- Ngày giờ: " + (appointment.getAppointmentDate() != null ? appointment.getAppointmentDate() : "Chưa xác định") + "\n"
                + "- Dịch vụ: " + services + "\n"
                + "- Trạng thái: Đang chờ xác nhận\n\n"
                + "Lưu ý: Quý khách vui lòng đến trước giờ hẹn 10–15 phút để hoàn tất thủ tục tiếp nhận.\n"
                + "Nếu cần hỗ trợ thay đổi thời gian hoặc có thắc mắc, vui lòng phản hồi email này.\n\n"
                + "Trân trọng,\nEV Service Center";
    }

    private String safe(String s) {
        return s == null ? "N/A" : s;
    }
}