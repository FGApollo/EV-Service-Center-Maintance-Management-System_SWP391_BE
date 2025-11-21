package com.example.Ev.System.service;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.dto.PartUsageDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.entity.AppointmentService;
import com.example.Ev.System.mapper.MaintainanceRecordMapper;
import com.example.Ev.System.mapper.PartUsageMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaintenanceRecordService {

    private final AppointmentRepository appointmentRepository;
    private final PartRepository partRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final MaintainanceRecordMapper maintainanceRecordMapper;
    private final UserRepository userRepository;
    private final PartUsageMapper partUsageMapper;
    private final PartUsageService partUsageService;

    public MaintenanceRecordService(AppointmentRepository appointmentRepository,
                                    PartRepository partRepository,

                                    MaintenanceRecordRepository maintenanceRecordRepository,
                                    MaintainanceRecordMapper maintainanceRecordMapper, UserRepository userRepository, PartUsageMapper partUsageMapper, PartUsageService partUsageService) {
        this.appointmentRepository = appointmentRepository;
        this.partRepository = partRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.maintainanceRecordMapper = maintainanceRecordMapper;
        this.userRepository = userRepository;
        this.partUsageMapper = partUsageMapper;
        this.partUsageService = partUsageService;
    }

    @Transactional
    public void recordMaintenance(Integer appointmentId, MaintainanceRecordDto maintainanceRecordDto, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointmentCheck = appointmentRepository.findById(appointmentId).orElse(null);

        if (appointmentCheck == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if (!appointmentCheck.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }
        for(Integer staffId : maintainanceRecordDto.getStaffIds()) {
            User user = userRepository.findById(staffId).orElse(null);
            if(!user.getServiceCenter().getId().equals(centerId)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
            }
            if (!"technician".equalsIgnoreCase(user.getRole())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a technician: " + staffId);
            }
        }

        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        List<Integer> staffIds = maintainanceRecordDto.getStaffIds();
        if (staffIds == null || staffIds.isEmpty()) {
            throw new RuntimeException("No technicians assigned");
        }

        for (Integer staffId : staffIds) {
            userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));
        }

        MaintenanceRecord record = maintainanceRecordMapper.toEntity(maintainanceRecordDto);
        record.setAppointment(appointment);
        record.setVehicleCondition(maintainanceRecordDto.getVehicleCondition());
        record.setChecklist(maintainanceRecordDto.getChecklist());
        record.setRemarks(maintainanceRecordDto.getRemarks());
        record.setStartTime(appointment.getAppointmentDate());

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
        record.setPartUsages(partUsages);
        for (PartUsageDto partUsageDto : maintainanceRecordDto.getPartsUsed()) {
            PartUsage partUsage = partUsageMapper.toEntity(partUsageDto);
            if (partUsage.getPart() == null) {
                System.out.println("⚠️ partUsage part is null for usageId: ");
                continue;
            }
            partUsageService.usePathNoUsage(
                    partUsage.getPart().getId().intValue(),
                    partUsage.getQuantityUsed(),
                    appointment.getServiceCenter().getId()
            );
        }
        maintenanceRecordRepository.save(record); // Cascade saves all part usages
    }

    @Transactional
    public void updateMaintainanceRecord(Integer appointmentID, MaintainanceRecordDto maintainanceRecordDto, int status,Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointmentCheck = appointmentRepository.findById(appointmentID).orElse(null);

        if (appointmentCheck == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if (!appointmentCheck.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }
        for(Integer staffId : maintainanceRecordDto.getStaffIds()) {
            User user = userRepository.findById(staffId).orElse(null);
            if(!user.getServiceCenter().getId().equals(centerId)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
            }
        }

        System.out.println("da chay toi day");
        MaintenanceRecord existMaintenanceRecord = maintenanceRecordRepository.findFirstByAppointment_IdOrderByIdDesc(appointmentID)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found for appointment ID: " + appointmentID));
        MaintenanceRecord maintenanceRecord = new MaintenanceRecord();
        maintenanceRecord.setAppointment(existMaintenanceRecord.getAppointment());
        maintenanceRecord.setVehicleCondition(maintainanceRecordDto.getVehicleCondition());
        maintenanceRecord.setChecklist(
                existMaintenanceRecord.getChecklist() + " | " + maintainanceRecordDto.getChecklist()
        );
        maintenanceRecord.setStartTime(existMaintenanceRecord.getStartTime());

        maintenanceRecord.setRemarks(
                existMaintenanceRecord.getRemarks() + " | " + maintainanceRecordDto.getRemarks()
        );

        List<Integer> staffIds = maintainanceRecordDto.getStaffIds();

        Set<Integer> oldStaffSet = new HashSet<>();
        if (existMaintenanceRecord.getTechnicianIds() != null && !existMaintenanceRecord.getTechnicianIds().isEmpty()) {
            oldStaffSet = Arrays.stream(existMaintenanceRecord.getTechnicianIds().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }
        Set<Integer> newStaffSet = new HashSet<>(staffIds);

        oldStaffSet.addAll(newStaffSet);

        String mergedTechIds = oldStaffSet.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        maintenanceRecord.setTechnicianIds(mergedTechIds);

        Set<PartUsage> oldPartUsages = existMaintenanceRecord.getPartUsages();
        List<PartUsageDto> updatedUsageDtos = maintainanceRecordDto.getPartsUsed();
        System.out.println(updatedUsageDtos.size());
        Set<PartUsage> newPartUsages = new LinkedHashSet<>();
        if (maintainanceRecordDto.getPartsUsed() != null) {
            for (PartUsageDto partUsageDto : maintainanceRecordDto.getPartsUsed()) {
                Part part = partRepository.findById(partUsageDto.getPartId())
                        .orElseThrow(() -> new RuntimeException("Part not found: " + partUsageDto.getPartId()));
                PartUsage newUsage = new PartUsage();
                newUsage.setPart(part);
                PartUsage temp = oldPartUsages.stream().
                        filter(pu -> pu.getPart().getId().equals(partUsageDto.getPartId().longValue())).findFirst().orElse(null);
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
        maintenanceRecord.setPartUsages(newPartUsages);
        MaintenanceRecord saved = maintenanceRecordRepository.saveAndFlush(maintenanceRecord);
        for (PartUsageDto partUsageDto : maintainanceRecordDto.getPartsUsed()) {
            PartUsage partUsage = partUsageMapper.toEntity(partUsageDto);
            if (partUsage.getPart() == null) {
                System.out.println("⚠️ partUsage part is null for usageId: ");
                continue;
            }
            partUsageService.usePathNoUsage(
                    partUsage.getPart().getId().intValue(),
                    partUsage.getQuantityUsed(),
                    saved.getAppointment().getServiceCenter().getId()
            );
        }
        if (status == 1) {
            saved.setEndTime(Instant.now());
        }
    }

    public void getPartUsageByAppointmentId(ServiceAppointment appointment) {

        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.findFirstByAppointment_IdOrderByIdDesc(appointment.getId()).orElse(null);
        Set<PartUsage> partUsages = maintenanceRecord.getPartUsages();
        for(PartUsage partUsage : partUsages){
            partUsageService.usePathNoUsage(partUsage.getPart().getId().intValue(),partUsage.getQuantityUsed(),appointment.getServiceCenter().getId());
        }
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

    @Transactional
    public List<MaintenanceRecord> getAll(List<ServiceAppointment> serviceAppointments) {
        List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();
        for(ServiceAppointment serviceAppointment : serviceAppointments){
            MaintenanceRecord temp = maintenanceRecordRepository.findFirstByAppointment_IdOrderByIdDesc(serviceAppointment.getId()).orElse(null);
            if(temp != null){
                maintenanceRecords.add(temp);
            }
        }
        return maintenanceRecords;
    }

    @Transactional
    public MaintenanceRecord getAllByAppointmentId(Integer appointmentId) {
        MaintenanceRecord maintenanceRecords = maintenanceRecordRepository.findFirstByAppointment_IdOrderByIdDesc(appointmentId).orElse(null);
        return maintenanceRecords;
    }





}
