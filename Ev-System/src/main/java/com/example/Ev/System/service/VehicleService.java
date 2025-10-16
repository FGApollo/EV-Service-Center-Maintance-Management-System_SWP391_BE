package com.example.Ev.System.service;

import com.example.Ev.System.dto.VehicleDto;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.repository.VehicleRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepo, UserRepository userRepo){
        this.vehicleRepository = vehicleRepo;
        this.userRepository = userRepo;
    }

    private VehicleDto toDto(Vehicle v) {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(v.getId());
        vehicleDto.setVin(v.getVin());
        vehicleDto.setModel(v.getModel());
        vehicleDto.setYear(v.getYear());
        vehicleDto.setBatteryCapacity(v.getBatteryCapacity());

        return vehicleDto;

    }

    public List<VehicleDto> getUserVehicle(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new RuntimeException("User not found");
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
            throw new RuntimeException("User not found");
        }

        Vehicle v = new Vehicle();
        v.setCustomer(user.get());
        v.setVin(dto.getVin());
        v.setModel(dto.getModel());
        v.setYear(dto.getYear());
        v.setBatteryCapacity(dto.getBatteryCapacity());
        v.setDeleted(false);
        v.setCreatedAt(Instant.now());
        vehicleRepository.save(v);

        return toDto(v);
    }

    public void deleteVehicle(String email, Integer id){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new RuntimeException("User not found");
        }

        Vehicle v = vehicleRepository.findVehicleById(id);
        if(v == null){
            throw new RuntimeException("Vehicle not found");
        }

        if(!v.getCustomer().getId().equals(user.get().getId())){
            throw new RuntimeException("Not authorized");
        }

        v.setDeleted(true);
        vehicleRepository.save(v);

    }
}
