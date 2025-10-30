package com.example.Ev.System.service;

import com.example.Ev.System.dto.VehicleDto;
import com.example.Ev.System.dto.VehicleRespone;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.ServiceType;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.repository.VehicleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public VehicleService(VehicleRepository vehicleRepo, UserRepository userRepo, AppointmentRepository appointmentRepository){
        this.vehicleRepository = vehicleRepo;
        this.userRepository = userRepo;
        this.appointmentRepository = appointmentRepository;
    }



    private VehicleDto toDto(Vehicle v) {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(v.getId());
        vehicleDto.setVin(v.getVin());
        vehicleDto.setModel(v.getModel());
        vehicleDto.setYear(v.getYear());
        vehicleDto.setColor(v.getColor());
        vehicleDto.setLicensePlate(v.getLicensePlate());
        return vehicleDto;

    }

    public List<VehicleDto> getUserVehicle(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }

        List<Vehicle> vehicle = vehicleRepository.findVehicleByCustomerAndDeleted(user.get(), false );

        List<VehicleDto> vehicleDto = new ArrayList<>();
        for(Vehicle x : vehicle){
            vehicleDto.add(toDto(x));
        }

        return vehicleDto;
    }

    @Transactional
    public VehicleDto addVehicle(String email, VehicleDto dto){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }

        Vehicle v = new Vehicle();
        v.setCustomer(user.get());
        v.setVin(dto.getVin());
        v.setModel(dto.getModel());
        v.setYear(dto.getYear());
        v.setColor(dto.getColor());
        v.setLicensePlate(dto.getLicensePlate());
        v.setDeleted(false);
        v.setCreatedAt(Instant.now());
        vehicleRepository.save(v);

        return toDto(v);
    }

    public void deleteVehicle(String email, Integer id){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }

        Vehicle v = vehicleRepository.findVehicleById(id);
        if(v == null){
            throw new NotFoundException("Vehicle not found");
        }

        if(!v.getCustomer().getId().equals(user.get().getId())){
            throw new BadRequestException("Not authorized");
        }

        v.setDeleted(true);
        vehicleRepository.save(v);

    }

    @Transactional
    public List<VehicleRespone> getVeicleResponeByCurrentCustomer(Authentication authentication){
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Vehicle> vehicles = vehicleRepository.findVehicleByCustomer(user);
        List<VehicleRespone> responses = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            ServiceAppointment serviceAppointment = appointmentRepository.findFirstByVehicleOrderByCreatedAtDesc(vehicle);
            if(serviceAppointment != null){
                VehicleRespone response = new VehicleRespone();
                response.setVehicleId(vehicle.getId());
                response.setModel(vehicle.getModel());
                response.setYear(vehicle.getYear());
                response.setVin(vehicle.getVin());
                response.setClosetTime(serviceAppointment.getCreatedAt());
                //response.setLicensePlate(vehicle.getLicensePlate());
                //response.setColor(vehicle.getColor());
                response.setOwnerName(user.getFullName());
                int number = numberOfCareByCar(vehicle);
                response.setMaintenanceCount(number);
                Set<ServiceType> serviceTypes = serviceAppointment.getServiceTypes();
                List<String> serviceString = new ArrayList<>();
                for (ServiceType serviceType : serviceTypes) {
                    serviceString.add(serviceType.getName());
                }
                response.setMaintenanceServices(serviceString);
                responses.add(response);
            }

        }
        return responses;
    }

    public int numberOfCareByCar(Vehicle vehicle) {
        int number = 0;
        List<ServiceAppointment> serviceAppointments = appointmentRepository.findAllByVehicle(vehicle);
        for(ServiceAppointment serviceAppointment : serviceAppointments){
            if(serviceAppointment.getStatus().equals("done")){
                number++;
            }
        }
        return number;
    }

    public ServiceAppointment findServiceAppointmentByVehicle(Vehicle vehicle){
        return appointmentRepository.findFirstByVehicleOrderByCreatedAtDesc(vehicle);
    }


}
