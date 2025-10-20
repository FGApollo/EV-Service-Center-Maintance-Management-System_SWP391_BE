package com.example.Ev.System.service;

import com.example.Ev.System.dto.AppointmentStatusDTO;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentStatusService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;


    public List<AppointmentStatusDTO> getUserAppointment(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }

        List<ServiceAppointment> appointments = appointmentRepository.findByCustomerOrderByAppointmentDateDesc(user.get());

        List<AppointmentStatusDTO> appointmentStatusDTO = new ArrayList<>();
        for(ServiceAppointment sa : appointments){
            appointmentStatusDTO.add(toDto(sa));
        }
        return appointmentStatusDTO;
    }

    private AppointmentStatusDTO toDto(ServiceAppointment sa){
        AppointmentStatusDTO dto = new AppointmentStatusDTO();
        dto.setAppointmentId(sa.getId());
        dto.setAppointmentDate(sa.getAppointmentDate());
        dto.setStatus(sa.getStatus());
        dto.setServiceCenterName(sa.getServiceCenter().getName());
        dto.setVehicleModel(sa.getVehicle().getModel());
        return dto;
    }
}
