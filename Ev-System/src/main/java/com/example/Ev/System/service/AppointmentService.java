package com.example.Ev.System.service;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.entity.ServiceType;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mappers.AppointmentMapper;
import com.example.Ev.System.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public AppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository, UserRepository userRepository, ServiceCenterRepository serviceCenterRepository, VehicleRepository vehicleRepository, ServiceTypeRepository serviceTypeRepository) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.serviceCenterRepository = serviceCenterRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public AppointmentDto createAppointment(AppointmentDto appointmentDto,
                                            Authentication authentication)
    {
        var appointment = appointmentMapper.toEntity(appointmentDto);
        String email = authentication.getName();
        var userOpt = userRepository.findByEmail(email);
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            appointment.setCustomer(user);
            appointment.setVehicle(vehicleRepository.findById(appointment.getVehicle().getId()).orElse(null));
            appointment.setServiceCenter(serviceCenterRepository.findById(appointment.getServiceCenter().getId()).orElse(null));
            appointment.setStatus("pending");
            List<ServiceType> serviceTypes = serviceTypeRepository.findAllById(appointmentDto.getServiceTypeIds());
            // Link services
            appointment.getServiceTypes().addAll(serviceTypes);
            appointmentRepository.save(appointment);
        }
        return appointmentDto;
    }
}
