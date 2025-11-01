package com.example.Ev.System.service;

import com.example.Ev.System.dto.AppointmentResponse;
import com.example.Ev.System.dto.VehicleRespone;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mapper.AppointmentMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public ServiceAppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository, UserRepository userRepository, ServiceCenterRepository serviceCenterRepository, VehicleRepository vehicleRepository, ServiceTypeRepository serviceTypeRepository, AppointmentServiceRepository appointmentServiceRepository, StaffAppointmentService staffAppointmentService, MaintenanceRecordService maintenanceRecordService, NotificationProgressService notificationProgressService, MaintenanceRecordRepository maintenanceRecordRepository) {
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
        appointment.setStatus("accept");
        appointment.setCreatedAt(Instant.now());
        Integer serviceCenterId = appointment.getServiceCenter().getId();
        staffAppointmentService.autoAssignTechnician(
                appointmentId,
                "Auto assign technician",
                serviceCenterId,
                "in_progress"
        );
        appointmentRepository.save(appointment);

        notificationProgressService.sendAppointmentStatusChanged(appointment.getCustomer(), appointment, oldStatus, "accept"); //new
        return appointment;
        //da test dc nhung return cuc lau => phai sua
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

    public List<ServiceAppointment> getAppointmentsByStaffId(Integer staffId) {
        List<ServiceAppointment> appointments = appointmentRepository.findAllByStaffAssignments_staff_id(staffId);
        return appointments;
    }





}
