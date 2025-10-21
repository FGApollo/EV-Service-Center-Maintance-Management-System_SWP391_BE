package com.example.Ev.System.service;

import com.example.Ev.System.entity.*;
import com.example.Ev.System.mapper.AppointmentMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

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

    public ServiceAppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository, UserRepository userRepository, ServiceCenterRepository serviceCenterRepository, VehicleRepository vehicleRepository, ServiceTypeRepository serviceTypeRepository, AppointmentServiceRepository appointmentServiceRepository, StaffAppointmentService staffAppointmentService, MaintenanceRecordService maintenanceRecordService) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.serviceCenterRepository = serviceCenterRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.staffAppointmentService = staffAppointmentService;
        this.maintenanceRecordService = maintenanceRecordService;
    }

    public List<ServiceAppointment> getStatusAppointments(String status) {
        List<ServiceAppointment> appointments = appointmentRepository.findAllByStatus(status);
        return appointments;
    }

    @Transactional
    public ServiceAppointment acceptAppointment(Integer appointmentId) {
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        appointment.setStatus("accept");
        appointment.setCreatedAt(Instant.now());
        staffAppointmentService.autoAssignTechnician(appointmentId ,"Auto assign technician");
        appointmentRepository.save(appointment);
        return appointment;
    }

    @Transactional
    public ServiceAppointment updateAppointment(Integer appointmentId,String status) {
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
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
