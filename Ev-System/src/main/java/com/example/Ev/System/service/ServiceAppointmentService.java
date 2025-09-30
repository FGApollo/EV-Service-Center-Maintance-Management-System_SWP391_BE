package com.example.Ev.System.service;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mappers.AppointmentMapper;
import com.example.Ev.System.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public ServiceAppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository, UserRepository userRepository, ServiceCenterRepository serviceCenterRepository, VehicleRepository vehicleRepository, ServiceTypeRepository serviceTypeRepository, AppointmentServiceRepository appointmentServiceRepository) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.serviceCenterRepository = serviceCenterRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
    }

    public ServiceAppointment updateAppointment(Integer appointmentId) {
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        appointment.setStatus("progress");
        appointment.setCreatedAt(Instant.now());
        return null;
    }


//    public AppointmentDto createAppointment(AppointmentDto appointmentDto,
//                                            Authentication authentication)
//    {
//        var appointment = appointmentMapper.toEntity(appointmentDto);
//        String email = authentication.getName();
//        var userOpt = userRepository.findByEmail(email);
//        if(userOpt.isPresent()) {
//            User user = userOpt.get();
//            appointment.setCustomer(user);
//            appointment.setVehicle(vehicleRepository.findById(appointment.getVehicle().getId()).orElse(null));
//            appointment.setServiceCenter(serviceCenterRepository.findById(appointment.getServiceCenter().getId()).orElse(null));
//            appointment.setStatus("pending");
//            List<ServiceType> serviceTypes = serviceTypeRepository.findAllById(appointmentDto.getServiceTypeIds());
//            // Link services
//            appointment.getServiceTypes().addAll(serviceTypes);
//            appointmentRepository.save(appointment);
//        }
//        return appointmentDto;
//    }

    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        // 1. Map DTO to entity
        ServiceAppointment appointment = appointmentMapper.toEntity(appointmentDto);

        // 2. Create and save a new customer
        User customer = new User();
        customer.setFullName("New Customer");   // Replace with real info if needed
        customer.setEmail("newcustomer111111111111111@example.com");
        customer.setPhone("0123456789");
        customer.setPasswordHash("default_password_hash"); // hash properly in real case
        customer.setRole("CUSTOMER");
        customer.setStatus("active");
        customer = userRepository.save(customer);

        // 3. Set basic fields on appointment
        appointment.setCustomer(customer);
        appointment.setVehicle(vehicleRepository.findById(appointmentDto.getVehicleId()).orElse(null));
        appointment.setServiceCenter(serviceCenterRepository.findById(appointmentDto.getServiceCenterId()).orElse(null));
        appointment.setStatus("pending");
        appointment.setCreatedAt(Instant.now());

        // 4. Save appointment first to generate ID
        appointment = appointmentRepository.save(appointment);

        // 5. Retrieve all service types
        addServiceTypesToAppointment(appointment.getId(),appointmentDto.getServiceTypeIds());

        System.out.println("Appointment saved with multiple services.");

        return appointmentDto;
    }

    @Transactional
    public void addServiceTypesToAppointment(Integer appointmentId, Set<Integer> serviceTypeIds) {
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        for (Integer stId : serviceTypeIds) {
            ServiceType st = serviceTypeRepository.findById(stId)
                    .orElseThrow(() -> new RuntimeException("ServiceType not found"));
            AppointmentServiceId asId = new AppointmentServiceId(appointment.getId(), st.getId()); //asId la 2 foreign key cho bang AppointmentService
            boolean alreadyExists = appointmentServiceRepository.existsById(asId);
            if (!alreadyExists) {
                AppointmentService as = new AppointmentService(asId, appointment, st);
                appointmentServiceRepository.save(as);   // 🔑 save child directly
            }
        }
    }



}
