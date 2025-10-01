package com.example.Ev.System.service;

import com.example.Ev.System.dto.WorkLogDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.WorkLog;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.repository.WorkLogRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkLogService {
    private final WorkLogRepository workLogRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public WorkLogService(WorkLogRepository workLogRepository,
                          UserRepository userRepository,
                          AppointmentRepository appointmentRepository) {
        this.workLogRepository = workLogRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public WorkLog createWorkLog(WorkLogDto dto) {
        User staff = userRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        ServiceAppointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        WorkLog workLog = new WorkLog();
//        workLog.set(staff);
//        workLog.setAppointment(appointment);
//        workLog.setHoursSpent(dto.getHoursSpent());
//        workLog.setTasksDone(dto.getTasksDone());

        return workLogRepository.save(workLog);
    }
}
