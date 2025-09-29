package com.example.Ev.System.service;

import com.example.Ev.System.dto.AppointmentDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.mappers.AppointmentMapper;
import com.example.Ev.System.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        customer.setEmail("newcustomer1111@example.com");
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
        List<ServiceType> serviceTypes = serviceTypeRepository.findAllById(appointmentDto.getServiceTypeIds());

        // 6. Create AppointmentService entries
        for (ServiceType st : serviceTypes) {
            AppointmentServiceId apsId = new AppointmentServiceId();
            apsId.setAppointmentId(appointment.getId());
            apsId.setServiceTypeId(st.getId());

//            AppointmentService aps = new AppointmentService();
//            aps.setId(apsId);
//            aps.setAppointment(appointment);
//            aps.setServiceType(st);
//
//            appointment.getAppointmentServices().add(aps);
            //Lam cach nao do de add Appointment service vao ServiceAppointment
        }

        // 7. Save appointment again with services
        appointmentRepository.save(appointment);

        System.out.println("Appointment saved with multiple services.");

        return appointmentDto;
    }

}
