package com.example.Ev.System.service;

import com.example.Ev.System.dto.AppointmentRequest;
import com.example.Ev.System.dto.AppointmentResponse;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepo;
    private final AppointmentServiceRepository appointmentServiceRepo;
    private final ServiceCenterRepository serviceCenterRepo;
    private final ServiceTypeRepository serviceTypeRepos;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;


    public AppointmentService(AppointmentRepository appointmentRepo, AppointmentServiceRepository appointmentServiceRepo,
                              ServiceCenterRepository serviceCenterRepo, ServiceTypeRepository serviceTypeRepos,
                              VehicleRepository vehicleRepo, UserRepository userRepo) {
        this.appointmentRepo = appointmentRepo;
        this.appointmentServiceRepo = appointmentServiceRepo;
        this.serviceCenterRepo = serviceCenterRepo;
        this.serviceTypeRepos = serviceTypeRepos;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, String email) throws BadRequestException {
        Optional<User> user = userRepo.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }

        Vehicle vehicle = vehicleRepo.findVehicleById(request.getVehicleId());
        if(vehicle == null){
            throw new NotFoundException("Vehicle not found");
        }

        if(!vehicle.getCustomer().getId().equals(user.get().getId())){
            throw new BadRequestException("This vehicle not match your account");
        }

        Optional<ServiceCenter> serviceCenter = serviceCenterRepo.findById(request.getServiceCenterId());
        if(serviceCenter.isEmpty()){
            throw new BadRequestException("Center not found");
        }

        List<ServiceType> serviceTypeList = serviceTypeRepos.findAllById(request.getServiceTypeIds());
        if(serviceTypeList.size() != request.getServiceTypeIds().size()){
            throw new BadRequestException("1 or more service not valid");
        }

        boolean conflictSchedule = appointmentRepo.existsByServiceCenter_IdAndAppointmentDate(request.getServiceCenterId(), request.getAppointmentDate());
        if(conflictSchedule){
            throw new BadRequestException("This schedule have been book, please choose another schedule");
        }

        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setCustomer(user.get());
        appointment.setVehicle(vehicle);
        appointment.setServiceCenter(serviceCenter.get());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStatus("Pending");
        appointment.setCreatedAt(Instant.now());
        appointmentRepo.save(appointment);

        for(ServiceType st : serviceTypeList){
            com.example.Ev.System.entity.AppointmentService appointmentService = new com.example.Ev.System.entity.AppointmentService();
            appointmentService.setAppointment(appointment);
            appointmentService.setServiceType(st);
            appointmentServiceRepo.save(appointmentService);
        }

        return new AppointmentResponse(appointment.getId(),
                user.get().getFullName(),
                vehicle.getModel(),
                serviceCenter.get().getName(),
                appointment.getAppointmentDate(),
                serviceTypeList.stream().map(ServiceType::getName).collect(Collectors.toList()),
                appointment.getStatus());

    }
}
