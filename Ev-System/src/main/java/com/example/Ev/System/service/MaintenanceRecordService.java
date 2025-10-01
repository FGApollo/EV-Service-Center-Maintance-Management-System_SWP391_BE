package com.example.Ev.System.service;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mapper.MaintainanceRecordMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MaintenanceRecordService {

    private final AppointmentRepository appointmentRepository;
    private final PartRepository partRepository;
    private final PartyUsageRepository partyUsageRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final MaintainanceRecordMapper maintainanceRecordMapper;

    public MaintenanceRecordService(AppointmentRepository appointmentRepository,
                                    PartRepository partRepository,
                                    PartyUsageRepository partyUsageRepository,
                                    MaintenanceRecordRepository maintenanceRecordRepository, MaintainanceRecordMapper maintainanceRecordMapper) {
        this.appointmentRepository = appointmentRepository;
        this.partRepository = partRepository;
        this.partyUsageRepository = partyUsageRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.maintainanceRecordMapper = maintainanceRecordMapper;
    }

    @Transactional
    public void recordMaintenance(Integer appointmentId,
                                  MaintainanceRecordDto maintainanceRecordDto) {

        // Find appointment
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Create maintenance record
        Maintenancerecord record = maintainanceRecordMapper.toEntity(maintainanceRecordDto);
        record.setAppointment(appointment);
        record.setVehicleCondition(maintainanceRecordDto.getVehicleCondition());
        record.setChecklist(maintainanceRecordDto.getChecklist());
        record.setRemarks(maintainanceRecordDto.getRemarks());
        record.setStartTime(appointment.getAppointmentDate());
        record.setEndTime(Instant.now());

        // Snapshot technicians from StaffAssignment
        Set<User> technicians = appointment.getStaffAssignments()
                .stream()
                .map(StaffAssignment::getStaff)
                .collect(Collectors.toSet());
        record.setTechnicians(technicians);

        // Save the maintenance record first
        maintenanceRecordRepository.save(record);

        // Save part usage
        for (Map.Entry<Integer, Integer> entry : maintainanceRecordDto.getPartsUsed().entrySet()) {
            Part part = partRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Part not found"));

            Partyusage usage = new Partyusage();
            usage.setRecord(record);
            usage.setPart(part);
            usage.setQuantityUsed(entry.getValue());

            partyUsageRepository.save(usage);
        }
    }
}
