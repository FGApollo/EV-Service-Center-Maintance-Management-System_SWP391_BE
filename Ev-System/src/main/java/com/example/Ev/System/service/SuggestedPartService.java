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
            dto.setTotal_price(x.getPart().getUnitPrice() * x.getQuantity());
            dto.setQuantity(x.getQuantity());
            dto.setTechnician_note(x.getTechnicianNote());
            dto.setStatus(x.getStatus());
            dto.setPart_name(x.getPart().getName());
            dto.setUnit_price(x.getPart().getUnitPrice());
            dto.setPart_description(x.getPart().getDescription());
            dtos.add(dto);
        }


        return dtos;
    }

    @Transactional
    public SuggestPartDto  acceptSuggestedPart(Integer suggestedPartId) {
        SuggestedPart part = suggestedPartRepository.findById(suggestedPartId);
        if(part == null){
            throw new NotFoundException("Suggest part khong ton tai");
        }

        part.setStatus("accepted");
        suggestedPartRepository.save(part);

        SuggestPartDto dto = new SuggestPartDto();
        dto.setPart_name(part.getPart().getName());
        dto.setQuantity(part.getQuantity());
        dto.setUnit_price(part.getPart().getUnitPrice());
        dto.setTotal_price(part.getPart().getUnitPrice() * part.getQuantity());
        dto.setPart_description(part.getPart().getDescription());
        dto.setTechnician_note(part.getTechnicianNote());
        dto.setStatus(part.getStatus());

        return dto;
    }

    @Transactional
    public SuggestPartDto  denySuggestedPart(Integer suggestedPartId) {
        SuggestedPart part = suggestedPartRepository.findById(suggestedPartId);
        if(part == null){
            throw new NotFoundException("Suggest part khong ton tai");
        }
        part.setStatus("denied");
        suggestedPartRepository.save(part);

        SuggestPartDto dto = new SuggestPartDto();
        dto.setPart_name(part.getPart().getName());
        dto.setQuantity(part.getQuantity());
        dto.setUnit_price(part.getPart().getUnitPrice());
        dto.setTotal_price(part.getPart().getUnitPrice() * part.getQuantity());
        dto.setPart_description(part.getPart().getDescription());
        dto.setTechnician_note(part.getTechnicianNote());
        dto.setStatus(part.getStatus());

        return dto;
    }
}
