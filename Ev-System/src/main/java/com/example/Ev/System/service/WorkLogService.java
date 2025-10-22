package com.example.Ev.System.service;

import com.example.Ev.System.dto.WorkLogDto;
import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Worklog;
import com.example.Ev.System.mapper.WorkLogMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WorkLogService {
    private final WorkLogRepository workLogRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final WorkLogMapper workLogMapper;
    private final MaintenanceRecordRepository maintenanceRecordRepository;

    public WorkLogService(WorkLogRepository workLogRepository,
                          UserRepository userRepository,
                          AppointmentRepository appointmentRepository, StaffAssignmentRepository staffAssignmentRepository, WorkLogMapper workLogMapper, MaintenanceRecordRepository maintenanceRecordRepository) {
        this.workLogRepository = workLogRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
        this.workLogMapper = workLogMapper;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
    }

    @Transactional
    public List<Worklog> createWorkLog(WorkLogDto dto) {
        List<Worklog> workLogs = new ArrayList<>();
        ServiceAppointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        for(Integer staffId : dto.getStaffId()) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            Worklog workLog = new Worklog();
            workLog.setStaff(staff);
            workLog.setAppointment(appointment);
            workLog.setHoursSpent(dto.getHoursSpent());
            workLog.setTasksDone(dto.getTasksDone());
            workLog.setCreatedAt(Instant.now());
            workLogs.add(workLogRepository.save(workLog));
        }
        return workLogs;
    }

    @Transactional
    public List<WorkLogDto> autoCreateWorkLog(Integer appointmentId) {
        System.out.println("da chay o day");
        List<WorkLogDto> workLogDtos = new ArrayList<>();
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        List<User> techs = staffAssignmentRepository.findStaffByAppointmentId(appointmentId);
        System.out.println(techs.size());
        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.findFirstByAppointment_Id(appointmentId).orElseThrow(() -> new RuntimeException("Maintenance record not found"));
        for(User tech : techs) {
            Worklog workLog = new Worklog();
            workLog.setStaff(tech);
            workLog.setAppointment(appointment);
            if (maintenanceRecord.getStartTime() == null || maintenanceRecord.getEndTime() == null) {
                throw new RuntimeException("Start or End time is missing in MaintenanceRecord");
            }
            Duration duration = Duration.between(maintenanceRecord.getStartTime(), maintenanceRecord.getEndTime());
            BigDecimal minutes = BigDecimal.valueOf(duration.toMinutes());
            BigDecimal hours = minutes.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            workLog.setHoursSpent(hours);

            workLog.setTasksDone(maintenanceRecord.getChecklist());
            workLog.setCreatedAt(Instant.now());
            workLogRepository.save(workLog);
            WorkLogDto workLogDto = workLogMapper.toDto(workLog);
            workLogDtos.add(workLogDto);
        }
        return workLogDtos;
    }

}
