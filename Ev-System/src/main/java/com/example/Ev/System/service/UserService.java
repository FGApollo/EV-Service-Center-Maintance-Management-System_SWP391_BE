package com.example.Ev.System.service;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.dto.VehicleDto;
import com.example.Ev.System.dto.VehicleRespone;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.mapper.UserMapper;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.ServiceCenterRepository;
import com.example.Ev.System.repository.UserRepository;
import com.example.Ev.System.repository.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VehicleRepository vehicleRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    public UserService(UserMapper userMapper, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, VehicleRepository vehicleRepository, AppointmentRepository appointmentRepository, ServiceCenterRepository serviceCenterRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.vehicleRepository = vehicleRepository;
        this.appointmentRepository = appointmentRepository;
        this.serviceCenterRepository = serviceCenterRepository;
    }


    @Transactional
    public UserDto createUser(RegisterUserDto registerUserDto
    )
    {
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        var user = userMapper.toEntity2(registerUserDto);
        user.setRole("customer");
        user.setStatus("active");
        user.setCreatedAt(Instant.now());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);
        var userDto = userMapper.toDTO(user);
        return userDto;
    }

    @Transactional
    public List<UserDto> getAllByRole(String role,int id) {
        ServiceCenter serviceCenter = serviceCenterRepository.findById(id).get();
        List<User> userByRole = userRepository.findAllByRoleAndServiceCenter(role,serviceCenter);
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : userByRole) {
            UserDto userDto = userMapper.toDTO(user);

            List<Vehicle> vehicles = vehicleRepository.findVehicleByCustomer(user);
            List<VehicleRespone> vehicleResponses = new ArrayList<>();

            for (Vehicle vehicle : vehicles) {
                List<ServiceAppointment> appointmentServices = appointmentRepository.findAllByVehicle(vehicle);

                List<String> serviceTypes = new ArrayList<>();
                for (ServiceAppointment appointment : appointmentServices) {
                    for (ServiceType serviceType : appointment.getServiceTypes()) {
                        serviceTypes.add(serviceType.getName());
                    }
                }

                VehicleRespone vehicleRespone = new VehicleRespone(
                        vehicle.getId(),
                        vehicle.getModel(),
                        vehicle.getYear(),
                        vehicle.getVin(),
                       " vehicle.getLicensePlate()",
                        "vehicle.getColor()",
                        user.getFullName(),
                        appointmentServices.size(),
                        null,
                        serviceTypes
                );
                vehicleResponses.add(vehicleRespone);
            }

            userDto.setVehicles(vehicleResponses);
            userDtos.add(userDto);
        }

        return userDtos;
    }


    @Transactional
    public UserDto createEmployee(RegisterUserDto userDto,String role,int id){
        ServiceCenter serviceCenter = serviceCenterRepository.findById(id).orElse(null);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        var user = userMapper.toEntity2(userDto);
        user.setRole(role);
        user.setStatus("active");
        user.setCreatedAt(Instant.now());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setServiceCenter(serviceCenter);
        userRepository.save(user);
        var userDTO = userMapper.toDTO(user);
        return userDTO;
    }

    @Transactional
    public UserDto deleteAccount(Integer userID)
    {
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found with id: " + userID));
        user.setStatus("inactive");
        userRepository.save(user);
        UserDto userDTO = userMapper.toDTO(user);
        return userDTO;
    }




    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public List<UserDto> getAllCustomer(String role){
        List<User> customer = userRepository.findAllByRole(role);
        List<UserDto> userDtos = new ArrayList<>();

        if(customer.isEmpty()){
            throw new NotFoundException("Not Found");
        }

        for(User c : customer){
            UserDto dto = new UserDto();
            dto.setRole(c.getRole());
            dto.setPhone(c.getPhone());
            dto.setEmail(c.getEmail());
            dto.setFullName(c.getFullName());
            dto.setId(c.getId());

            List<VehicleRespone> vehicleResponeList = new ArrayList<>();
            if(c.getVehicles() != null){
                for(Vehicle v : c.getVehicles()){
                    VehicleRespone vr = new VehicleRespone();
                    vr.setVehicleId(v.getId());
                    vr.setVin(v.getVin());
                    vr.setYear(v.getYear());
                    vr.setLicensePlate(v.getLicensePlate());
                    vr.setModel(v.getModel());
                    vr.setColor(v.getColor());
                    vehicleResponeList.add(vr);
                }
            }
            dto.setVehicles(vehicleResponeList);

            userDtos.add(dto);
        }
        return userDtos;
    }
}


















