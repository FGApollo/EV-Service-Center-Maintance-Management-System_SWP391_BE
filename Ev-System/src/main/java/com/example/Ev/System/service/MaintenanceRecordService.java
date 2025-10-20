package com.example.Ev.System.service;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.dto.PartUsageDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mapper.MaintainanceRecordMapper;
import com.example.Ev.System.mapper.PartUsageMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaintenanceRecordService {

    private final AppointmentRepository appointmentRepository;
    private final PartRepository partRepository;
    private final PartyUsageRepository partyUsageRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final MaintainanceRecordMapper maintainanceRecordMapper;
    private final UserRepository userRepository;
    private final PartUsageMapper partUsageMapper;

    public MaintenanceRecordService(AppointmentRepository appointmentRepository,
                                    PartRepository partRepository,
                                    PartyUsageRepository partyUsageRepository,
                                    MaintenanceRecordRepository maintenanceRecordRepository,
                                    MaintainanceRecordMapper maintainanceRecordMapper, UserRepository userRepository, PartUsageMapper partUsageMapper) {
        this.appointmentRepository = appointmentRepository;
        this.partRepository = partRepository;
        this.partyUsageRepository = partyUsageRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.maintainanceRecordMapper = maintainanceRecordMapper;
        this.userRepository = userRepository;
        this.partUsageMapper = partUsageMapper;
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
        MaintenanceRecord record = maintainanceRecordMapper.toEntity(maintainanceRecordDto);
        record.setAppointment(appointment);
        record.setVehicleCondition(maintainanceRecordDto.getVehicleCondition());
        record.setChecklist(maintainanceRecordDto.getChecklist());
        record.setRemarks(maintainanceRecordDto.getRemarks());
        record.setStartTime(appointment.getAppointmentDate());
        record.setEndTime(Instant.now());

        String technicianIds = staffIds.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        record.setTechnicianIds(technicianIds);

        List<PartUsageDto> partUsageDtos = maintainanceRecordDto.getPartsUsed();
        Set<PartUsage> partUsages = new HashSet<>();

        if (partUsageDtos != null && !partUsageDtos.isEmpty()) {
            for (PartUsageDto partDto : partUsageDtos) {
                Part part = partRepository.findById(partDto.getPartId())
                        .orElseThrow(() -> new RuntimeException("Part not found: " + partDto.getPartId()));
                PartUsage partUsage = partUsageMapper.toEntity(partDto);
                // Set relationships manually
                partUsage.setRecord(record);
                partUsage.setPart(partRepository.findById(partDto.getPartId())
                        .orElseThrow(() -> new RuntimeException("Part not found")));
                partUsages.add(partUsage);
            }
        }

        record.setPartyusages(partUsages);
        maintenanceRecordRepository.save(record); // Cascade saves all part usages
    }

    @Transactional
    public void updateMaintainanceRecord(Integer appointmentID, MaintainanceRecordDto maintainanceRecordDto) {
        System.out.println("da chay toi day");
        MaintenanceRecord existMaintenanceRecord = maintenanceRecordRepository.findFirstByAppointment_Id(appointmentID)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found for appointment ID: " + appointmentID));
        MaintenanceRecord maintenanceRecord = new MaintenanceRecord();
        maintenanceRecord.setAppointment(existMaintenanceRecord.getAppointment());
        maintenanceRecord.setVehicleCondition(maintainanceRecordDto.getVehicleCondition());
        maintenanceRecord.setChecklist(existMaintenanceRecord + maintainanceRecordDto.getChecklist());
        maintenanceRecord.setStartTime(existMaintenanceRecord.getStartTime());
        maintenanceRecord.setRemarks(existMaintenanceRecord.getRemarks() + maintenanceRecord.getRemarks());

        List<Integer> staffIds = maintainanceRecordDto.getStaffIds();
        String technicianIds = staffIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        maintenanceRecord.setTechnicianIds(technicianIds);

        Set<PartUsage> oldPartUsages = existMaintenanceRecord.getPartyusages();
        List<PartUsageDto> updatedUsageDtos = maintainanceRecordDto.getPartsUsed();
        System.out.println(updatedUsageDtos.size());
        Set<PartUsage> oldPartUsage = existMaintenanceRecord.getPartyusages();
        Set<PartUsage> newPartUsages = new LinkedHashSet<>();
        if (maintainanceRecordDto.getPartsUsed() != null) {
            for (PartUsageDto partUsageDto : maintainanceRecordDto.getPartsUsed()) {
                Part part = partRepository.findById(partUsageDto.getPartId())
                        .orElseThrow(() -> new RuntimeException("Part not found: " + partUsageDto.getPartId()));
                PartUsage newUsage = new PartUsage();
                newUsage.setPart(part);
                PartUsage temp = oldPartUsage.stream().
                        filter(pu -> pu.getPart().getId().equals(partUsageDto.getPartId())).findFirst().orElse(null);
                if(temp != null){
                    newUsage.setQuantityUsed(partUsageDto.getQuantityUsed() + temp.getQuantityUsed());
                }
                else {
                    newUsage.setQuantityUsed(partUsageDto.getQuantityUsed());
                }
                newUsage.setUnitCost(partUsageDto.getUnitCost());
                newUsage.setRecord(maintenanceRecord);
                newPartUsages.add(newUsage);
            }
        }
        maintenanceRecord.setPartyusages(newPartUsages);
        MaintenanceRecord saved = maintenanceRecordRepository.save(maintenanceRecord);

    }

    public List<MaintainanceRecordDto> getMaintainanceRecordByStaff_id(String staffId) {
        List<MaintenanceRecord> maintenanceRecord = maintenanceRecordRepository.findByTechnicianId(staffId).orElse(null);
        List<MaintainanceRecordDto> maintainanceRecordDtos = new ArrayList<>();
        if (maintenanceRecord != null) {
            for(MaintenanceRecord temp : maintenanceRecord) {
                MaintainanceRecordDto maintainanceRecordDto = maintainanceRecordMapper.toDTO(temp);
                maintainanceRecordDtos.add(maintainanceRecordDto);
            }
        }
        return maintainanceRecordDtos;
    }

    public boolean findMaintainanceRecordByAppointmentId(Integer appointmentId) {
        Optional<MaintenanceRecord> maintenanceRecord = maintenanceRecordRepository.findFirstByAppointment_Id(appointmentId);
        return maintenanceRecord.isPresent();
    }

}
