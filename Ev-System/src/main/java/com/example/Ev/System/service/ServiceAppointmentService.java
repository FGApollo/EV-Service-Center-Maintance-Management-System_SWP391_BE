package com.example.Ev.System.service;

import com.example.Ev.System.dto.*;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.mapper.AppointmentMapper;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceAppointmentService {
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final StaffAppointmentService staffAppointmentService  ;
    private final MaintenanceRecordService maintenanceRecordService;
    private final NotificationProgressService notificationProgressService;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final UserService userService;
    private final MaintenanceReminderCreationService maintenanceReminderCreationService;
    private final WorkLogService workLogService;
    private final UserMapper userMapper;

    public ServiceAppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository, UserRepository userRepository, ServiceCenterRepository serviceCenterRepository, VehicleRepository vehicleRepository, ServiceTypeRepository serviceTypeRepository, AppointmentServiceRepository appointmentServiceRepository, StaffAppointmentService staffAppointmentService, MaintenanceRecordService maintenanceRecordService, NotificationProgressService notificationProgressService, MaintenanceRecordRepository maintenanceRecordRepository, UserService userService, MaintenanceReminderCreationService maintenanceReminderCreationService, WorkLogService workLogService, UserMapper userMapper) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.serviceCenterRepository = serviceCenterRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.staffAppointmentService = staffAppointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        this.notificationProgressService = notificationProgressService;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.userService = userService;
        this.maintenanceReminderCreationService = maintenanceReminderCreationService;
        this.workLogService = workLogService;
        this.userMapper = userMapper;
    }

    @Transactional
    public List<ServiceAppointment> getStatusAppointments(String status ,int serviceCenterId) {
        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId).orElse(null);
        List<ServiceAppointment> appointments = appointmentRepository.findAllByStatusAndServiceCenter(status,serviceCenter);
        return appointments;
    }

    @Transactional
    public ServiceAppointment acceptAppointment(Integer appointmentId) {
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        String oldStatus = appointment.getStatus();//new
        appointment.setStatus("accepted");
        appointment.setCreatedAt(Instant.now());
        Integer serviceCenterId = appointment.getServiceCenter().getId();
        appointmentRepository.save(appointment);
        notificationProgressService.sendAppointmentStatusChanged(appointment.getCustomer(), appointment, oldStatus, "accept"); //new
        return appointment;
    }

    @Transactional
    public ServiceAppointment updateAppointment(Integer appointmentId,String status) {
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        String oldStatus = appointment.getStatus(); //new
        appointment.setStatus(status);
        appointmentRepository.save(appointment);

        notificationProgressService.sendAppointmentStatusChanged(appointment.getCustomer(), appointment, oldStatus, status); //new
        return appointment;
    }

    @Transactional
    public ServiceAppointment doneAppointment(Integer appointmentId) {
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        return appointment;
    }

    @Transactional
    public List<ServiceAppointment> getAppointmentsByStaffId(Integer staffId) {
        List<ServiceAppointment> appointments = appointmentRepository.findAllByStaffAssignments_staff_id(staffId);
        return appointments;
    }
    @Transactional
    public ServiceAppointment findById(Integer appointmentId) {
        return appointmentRepository.findById(appointmentId).orElse(null);
    }

    @Transactional
    public ServiceAppointment getAppointmentWithAllDetails(Integer id) {
        return appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Transactional
    public List<ServiceAppointment> findAll(){
        return appointmentRepository.findAll();
    }

    @Transactional
    public List<ServiceAppointment> findAllByServiceCenter(ServiceCenter serviceCenter) {
        return  appointmentRepository.findAllByServiceCenter(serviceCenter);
    }

    public List<ServiceAppointment> findAllByServiceCenterId(Integer serviceCenterId) {
        return appointmentRepository.findAllByServiceCenter_Id(serviceCenterId);
    }

    public ServiceAppointment validateAndGetAppointmentForCenter( Authentication authentication ,Integer appointmentId) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }

        return appointment;
    }

    @Transactional
    public AppointmentResponse markAppointmentAsDone(Integer id,MaintainanceRecordDto maintainanceRecordDto,Authentication authentication) {
        validateAndGetAppointmentForCenter(authentication, id);
        ServiceAppointment updatedAppointment = updateAppointment(id, "completed");
        boolean recordExists = maintenanceRecordService.findMaintainanceRecordByAppointmentId(id);
        if (recordExists) {
            maintenanceRecordService.updateMaintainanceRecord(
                    updatedAppointment.getId(), maintainanceRecordDto, 1, authentication);
        } else {
            maintenanceRecordService.recordMaintenance(id, maintainanceRecordDto, authentication);
        }
        maintenanceReminderCreationService.createReminderForAppointmentIfDone(id);

        ServiceAppointment refreshed = getAppointmentWithAllDetails(id);
        AppointmentResponse response = appointmentMapper.toResponse(refreshed);

        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(id);
        String sId = staffIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        response.setTechIds(sId);

        workLogService.autoCreateWorkLog(id);

        return response;
    }

    @Transactional
    public AppointmentResponse markAppointmentInProgress(
            Integer id, Authentication authentication) {

        ServiceAppointment appointment = validateAndGetAppointmentForCenter(authentication, id);

        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(id);
        if (staffIdList.isEmpty()) {
            throw new BadRequestException("Chưa được giao việc cho kỹ thuật viên!");
        }

        if ("in_progress".equalsIgnoreCase(appointment.getStatus())) {
            throw new BadRequestException("Lịch hẹn này đã ở trạng thái đang thực hiện!");
        }


        ServiceAppointment updatedAppointment = updateAppointment(id, "in_progress");

        String sId = staffIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        AppointmentResponse response = appointmentMapper.toResponse(updatedAppointment);
        response.setTechIds(sId);

        List<User> techUsers = userService.getAllById(staffIdList);
        List<UserDto> techDto = userMapper.toDTOList(techUsers);
        response.setUsers(techDto);

        return response;
    }


    @Transactional
    public List<AppointmentResponse> getAppointmentsByStatusForCenter(String status, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        List<ServiceAppointment> appointments = getStatusAppointments(status, centerId);
        List<AppointmentResponse> responses = new ArrayList<>();
        for (ServiceAppointment appointment : appointments) {
            AppointmentResponse response = buildFullAppointmentResponse(appointment);
            responses.add(response);
        }
        return responses;
    }

    private AppointmentResponse buildFullAppointmentResponse(ServiceAppointment appointment) {
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        List<Integer> staffIdList = staffAppointmentService.staffIdsByAppointmentId(appointment.getId());
        if (staffIdList != null && !staffIdList.isEmpty()) {
            String sId = staffIdList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            response.setTechIds(sId);
            List<User> techUsers = userService.getAllById(staffIdList);
            response.setUsers(userMapper.toDTOList(techUsers));
        } else {
            response.setUsers(Collections.emptyList());
            response.setTechIds("");
        }

        if (appointment.getServiceTypes() != null && !appointment.getServiceTypes().isEmpty()) {
            List<String> serviceNames = appointment.getServiceTypes().stream()
                    .map(ServiceType::getName)
                    .collect(Collectors.toList());
            response.setServiceNames(serviceNames);

            int total = appointment.getServiceTypes().stream()
                    .mapToInt(serviceType -> serviceType.getPrice().intValue())
                    .sum();
            response.setTotal(total);
        }
        MaintenanceRecord maintenanceRecord = maintenanceRecordService.getAllByAppointmentId(appointment.getId());
        if (maintenanceRecord != null && maintenanceRecord.getChecklist() != null) {
            List<String> checklist = List.of(maintenanceRecord.getChecklist().split("\\s*,\\s*"));
            response.setCheckList(checklist);
        }
        return response;
    }

    @Transactional
    public AppointmentResponse getAppointmentDetailsById(Integer id, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appointment.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }
        return buildFullAppointmentResponse(appointment);
    }






//    @Transactional
//    public List<AppointmentAllFieldsDto> getAllAppointment(){
//        return appointmentRepository.findAll()
//                .stream()
//                .map(sa -> {
//                    AppointmentAllFieldsDto dto = new AppointmentAllFieldsDto();
//                    dto.setAppointmentId(sa.getId());
//                    dto.setCustomerId(sa.getCustomer().getId());
//                    dto.setStatus(sa.getStatus());
//                    dto.setCenterId(sa.getServiceCenter().getId());
//                    dto.setAppoimentDate(sa.getAppointmentDate());
//                    dto.setVehicleId(sa.getVehicle().getId());
//                    dto.setCreateAt(sa.getCreatedAt());
//                    dto.setEmail(sa.getCustomer().getEmail());
//                    dto.setPhone(sa.getCustomer().getPhone());
//                    dto.setFullName(sa.getCustomer().getFullName());
//
//                    String serviceTypeName = sa.getServiceTypes()
//                            .stream()
//                            .map(ServiceType::getName)
//                            .collect(Collectors.joining(", "));
//                    dto.setServiceType(serviceTypeName);
//
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//    }

    @Transactional
    public List<AppointmentAllFieldsDto> getAllAppointmentWithSpecificDetails(){
        List<ServiceAppointment> appointments = appointmentRepository.findAll();
        List<AppointmentAllFieldsDto> appointmentAllFieldsDtos = new ArrayList<>();

        for(ServiceAppointment sa : appointments){
            AppointmentAllFieldsDto dto = new AppointmentAllFieldsDto();
            dto.setAppointmentId(sa.getId());
            dto.setCustomerId(sa.getCustomer().getId());
            dto.setStatus(sa.getStatus());
            dto.setCenterId(sa.getServiceCenter().getId());
            dto.setAppoimentDate(sa.getAppointmentDate());
            dto.setVehicleId(sa.getVehicle().getId());
            dto.setCreateAt(sa.getCreatedAt());
            dto.setEmail(sa.getCustomer().getEmail());
            dto.setPhone(sa.getCustomer().getPhone());
            dto.setFullName(sa.getCustomer().getFullName());
            dto.setVehicleName(sa.getVehicle().getModel());
            dto.setVehicleVin(sa.getVehicle().getVin());
            dto.setVehicleLicensePlate( sa.getVehicle().getLicensePlate());

            BigDecimal cost = BigDecimal.ZERO;
            for(ServiceType st : sa.getServiceTypes()){

                cost = cost.add(st.getPrice());
            }

            dto.setCost(cost);

            String serviceTypeName = sa.getServiceTypes()
                           .stream()
                           .map(ServiceType::getName)
                           .collect(Collectors.joining(", "));
                 dto.setServiceType(serviceTypeName);



            appointmentAllFieldsDtos.add(dto);
        }

        return appointmentAllFieldsDtos;
    }

    
}
