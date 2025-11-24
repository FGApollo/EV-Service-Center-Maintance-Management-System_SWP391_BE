package com.example.Ev.System.service;

import com.example.Ev.System.dto.SuggestPartDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.SuggestedPart;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.ServiceAppointmentRepository;
import com.example.Ev.System.repository.SuggestedPartRepository;
import com.example.Ev.System.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SuggestedPartService {
    private final SuggestedPartRepository suggestedPartRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public SuggestedPartService(SuggestedPartRepository suggestedPartRepository, UserRepository userRepository, AppointmentRepository appointmentRepository){
        this.suggestedPartRepository = suggestedPartRepository;
        this.userRepository = userRepository;

        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public List<SuggestPartDto> getAllSuggestPartForAppointment(Integer appointmentId){

        List<SuggestedPart> suggestedPart = suggestedPartRepository.findAllByAppointment_Id(appointmentId);
        List<SuggestPartDto> dtos = new ArrayList<>();

        for(SuggestedPart x : suggestedPart){
            SuggestPartDto dto = new SuggestPartDto();
            dto.setPart_price(x.getPart().getUnitPrice() * x.getQuantity());
            dto.setQuantity(x.getQuantity());
            dto.setTechnician_note(x.getTechnicianNote());
            dto.setStatus(x.getStatus());
            dto.setPart_name(x.getPart().getName());
            dtos.add(dto);
        }


        return dtos;
    }
}
