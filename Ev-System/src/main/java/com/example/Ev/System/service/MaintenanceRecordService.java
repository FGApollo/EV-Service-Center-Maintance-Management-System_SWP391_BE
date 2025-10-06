package com.example.Ev.System.service;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.dto.PartUsageDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mapper.MaintainanceRecordMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
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
    private final UserRepository userRepository;

    public MaintenanceRecordService(AppointmentRepository appointmentRepository,
                                    PartRepository partRepository,
                                    PartyUsageRepository partyUsageRepository,
                                    MaintenanceRecordRepository maintenanceRecordRepository,
                                    MaintainanceRecordMapper maintainanceRecordMapper, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.partRepository = partRepository;
        this.partyUsageRepository = partyUsageRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.maintainanceRecordMapper = maintainanceRecordMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public void recordMaintenance(Integer appointmentId, MaintainanceRecordDto maintainanceRecordDto) {

        // 🔹 Find appointment
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // 🔹 Collect technician IDs
        List<Integer> staffIds = maintainanceRecordDto.getStaffIds();
        if (staffIds == null || staffIds.isEmpty()) {
            throw new RuntimeException("No technicians assigned");
        }

        // Validate staff exist
        for (Integer staffId : staffIds) {
            userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));
        }

        // 🔹 Create ONE maintenance record for all technicians
        Maintenancerecord record = maintainanceRecordMapper.toEntity(maintainanceRecordDto);
        record.setAppointment(appointment);
        record.setVehicleCondition(maintainanceRecordDto.getVehicleCondition());
        record.setChecklist(maintainanceRecordDto.getChecklist());
        record.setRemarks(maintainanceRecordDto.getRemarks());
        record.setStartTime(appointment.getAppointmentDate());
        record.setEndTime(Instant.now());

        // Convert technician IDs to comma-separated string
        String technicianIds = staffIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        record.setTechnicianIds(technicianIds);

        // 🔹 Handle part usages
        List<PartUsageDto> partUsageDtos = maintainanceRecordDto.getPartsUsed();
        Set<Partyusage> partUsages = new HashSet<>();

        if (partUsageDtos != null && !partUsageDtos.isEmpty()) {
            for (PartUsageDto partDto : partUsageDtos) {
                Part part = partRepository.findById(partDto.getPartId())
                        .orElseThrow(() -> new RuntimeException("Part not found: " + partDto.getPartId()));

                Partyusage usage = new Partyusage();
                usage.setRecord(record);
                usage.setPart(part);
                usage.setQuantityUsed(partDto.getQuantityUsed());
                usage.setUnitCost(partDto.getUnitCost());
                partUsages.add(usage);
            }
        }

        record.setPartyusages(partUsages);
        maintenanceRecordRepository.save(record); // Cascade saves all part usages
    }

}
