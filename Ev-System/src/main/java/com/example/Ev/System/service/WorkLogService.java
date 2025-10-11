package com.example.Ev.System.service;

import com.example.Ev.System.dto.WorkLogDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.WorkLog;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.repository.WorkLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public List<WorkLog> createWorkLog(WorkLogDto dto) {
        List<WorkLog> workLogs = new ArrayList<>();
        ServiceAppointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        for(Integer staffId : dto.getStaffId()) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            WorkLog workLog = new WorkLog();
            workLog.setStaff(staff);
            workLog.setAppointment(appointment);
            workLog.setHoursSpent(dto.getHoursSpent());
            workLog.setTasksDone(dto.getTasksDone());
            workLog.setCreatedAt(Instant.now());
            workLogs.add(workLogRepository.save(workLog));
        }
        return workLogs;
    }
}
