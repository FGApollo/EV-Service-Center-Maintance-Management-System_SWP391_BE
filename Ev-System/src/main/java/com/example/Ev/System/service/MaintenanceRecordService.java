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
    public void recordMaintenance(Integer appointmentId, MaintainanceRecordDto maintainanceRecordDto, Authentication authentication,int status) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointmentCheck = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointmentCheck == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        if(appointmentCheck.getStatus().equals("completed")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is done , cannot be updated");
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
            PartUsage partUsage = mapDtoToEntityWithPart(partUsageDto);
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
        if(status == 1){
            record.setEndTime(Instant.now());
            maintenanceRecordRepository.save(record);
        }
        maintenanceRecordRepository.save(record);
    }

    @Transactional
    public void updateMaintainanceRecord(Integer appointmentID, MaintainanceRecordDto maintainanceRecordDto, int status, Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointmentCheck = appointmentRepository.findById(appointmentID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (appointmentCheck.getStatus().equals("completed")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is done, cannot be updated");
        }

        if (!appointmentCheck.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }

        // Check staff access
        for (Integer staffId : maintainanceRecordDto.getStaffIds()) {
            User user = userRepository.findById(staffId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found: " + staffId));
            if (!user.getServiceCenter().getId().equals(centerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Staff not in your center");
            }
        }

        // Fetch existing maintenance record
        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.findFirstByAppointment_IdOrderByIdDesc(appointmentID)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found for appointment ID: " + appointmentID));

        maintenanceRecord.setVehicleCondition(maintainanceRecordDto.getVehicleCondition());
        Set<String> oldChecklistSet = new LinkedHashSet<>();
        if (maintenanceRecord.getChecklist() != null && !maintenanceRecord.getChecklist().isEmpty()) {
            oldChecklistSet = Arrays.stream(maintenanceRecord.getChecklist().split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        Set<String> newChecklistSet = new LinkedHashSet<>();
        if(maintainanceRecordDto.getChecklist() != null && !maintainanceRecordDto.getChecklist().isEmpty()) {
            newChecklistSet = Arrays.stream(maintainanceRecordDto.getChecklist().split("\\|")).map(String::trim)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        newChecklistSet.addAll(oldChecklistSet);
        String mergeCheckList = newChecklistSet.stream().collect(Collectors.joining("|"));
        maintenanceRecord.setChecklist(mergeCheckList);
        maintenanceRecord.setRemarks(maintainanceRecordDto.getRemarks());

        Set<Integer> oldStaffSet = new HashSet<>();
        if (maintenanceRecord.getTechnicianIds() != null && !maintenanceRecord.getTechnicianIds().isEmpty()) {
            oldStaffSet = Arrays.stream(maintenanceRecord.getTechnicianIds().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }
        oldStaffSet.addAll(new HashSet<>(maintainanceRecordDto.getStaffIds()));
        String mergedTechIds = oldStaffSet.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        maintenanceRecord.setTechnicianIds(mergedTechIds);

        Map<Integer, PartUsage> existingPartsMap = maintenanceRecord.getPartUsages().stream()
                .collect(Collectors.toMap(pu -> pu.getPart().getId(), pu -> pu));

        Set<PartUsage> updatedPartUsages = new LinkedHashSet<>();
        if (maintainanceRecordDto.getPartsUsed() != null) {
            for (PartUsageDto dto : maintainanceRecordDto.getPartsUsed()) {
                Part part = partRepository.findById(dto.getPartId())
                        .orElseThrow(() -> new RuntimeException("Part not found: " + dto.getPartId()));

                PartUsage pu = existingPartsMap.getOrDefault(part.getId(), new PartUsage());
                pu.setPart(part);
                pu.setRecord(maintenanceRecord);
                pu.setUnitCost(dto.getUnitCost());
                pu.setQuantityUsed(pu.getQuantityUsed()+ dto.getQuantityUsed());
                updatedPartUsages.add(pu);
            }
        }

        Set<PartUsage> existingPartUsages = maintenanceRecord.getPartUsages();
        existingPartUsages.clear();
        existingPartUsages.addAll(updatedPartUsages);

        if (status == 1) {
            maintenanceRecord.setEndTime(Instant.now());
        }

        maintenanceRecordRepository.saveAndFlush(maintenanceRecord);

        for (PartUsageDto dtoUsage : maintainanceRecordDto.getPartsUsed()) {
            partUsageService.usePathNoUsage(dtoUsage.getPartId(), dtoUsage.getQuantityUsed(),
                    maintenanceRecord.getAppointment().getServiceCenter().getId());
        }
        //da xong
    }

    public List<PartUsageDto> getPartUsageByAppointmentId(ServiceAppointment appointment) {
        MaintenanceRecord maintenanceRecord =
                maintenanceRecordRepository.findFirstByAppointment_IdOrderByIdDesc(appointment.getId())
                        .orElse(null);

        if (maintenanceRecord == null || maintenanceRecord.getPartUsages() == null) {
            return List.of();
        }

        List<PartUsageDto> dtoList = maintenanceRecord.getPartUsages()
                .stream()
                .map(partUsageMapper::toDto)
                .toList();
        return dtoList;
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

    @Transactional
    public PartUsage mapDtoToEntityWithPart(PartUsageDto dto) {
        PartUsage usage = partUsageMapper.toEntity(dto);

        usage.setPart(
                partRepository.findById(dto.getPartId())
                        .orElseThrow(() -> new RuntimeException("Part not found: " + dto.getPartId()))
        );

        return usage;
    }

    @Transactional
    public void flush() {
        maintenanceRecordRepository.flush();
    }





}
