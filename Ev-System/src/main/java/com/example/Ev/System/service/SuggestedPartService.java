package com.example.Ev.System.service;

import com.example.Ev.System.dto.RequestSuggestPart;
import com.example.Ev.System.dto.SuggestPartDto;
import com.example.Ev.System.entity.*;
import com.example.Ev.System.entity.AppointmentService;
import com.example.Ev.System.exception.BadRequestException;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.mapper.SuggestPartMapper;
import com.example.Ev.System.repository.*;
import org.apache.coyote.Request;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SuggestedPartService {
    private final SuggestedPartRepository suggestedPartRepository;
    private final SuggestPartMapper suggestPartMapper;
    private final ServiceAppointmentService serviceAppointmentService;
    private final PartService partService;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    public SuggestedPartService(SuggestedPartRepository suggestedPartRepository, SuggestPartMapper suggestPartMapper, ServiceAppointmentService serviceAppointmentService, PartService partService, PartRepository partRepository, UserRepository userRepository){
        this.suggestedPartRepository = suggestedPartRepository;
        this.suggestPartMapper = suggestPartMapper;
        this.serviceAppointmentService = serviceAppointmentService;
        this.partService = partService;
        this.partRepository = partRepository;
        this.userRepository = userRepository;
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
            dto.setPartId(x.getPart().getId());
            dto.setAppointmentId(x.getAppointment().getId());
            dto.setSuggestPart_Id(x.getId());
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
        dto.setPartId(part.getPart().getId());
        dto.setAppointmentId(part.getAppointment().getId());

        return dto;
    }

    @Transactional
    public SuggestPartDto  denySuggestedPart(Integer suggestedPartId) {
        SuggestedPart part = suggestedPartRepository.findById(suggestedPartId);
        if(part == null){
            throw new NotFoundException("Suggest part khong ton tai");
        }
        part.setStatus("rejected");
        suggestedPartRepository.save(part);

        SuggestPartDto dto = new SuggestPartDto();
        dto.setPart_name(part.getPart().getName());
        dto.setQuantity(part.getQuantity());
        dto.setUnit_price(part.getPart().getUnitPrice());
        dto.setTotal_price(part.getPart().getUnitPrice() * part.getQuantity());
        dto.setPart_description(part.getPart().getDescription());
        dto.setTechnician_note(part.getTechnicianNote());
        dto.setStatus(part.getStatus());
        dto.setPartId(part.getPart().getId());
        dto.setAppointmentId(part.getAppointment().getId());
        return dto;
    }

    @Transactional
    public List<SuggestPartDto> getAllSuggestPartForCurrentCustomer(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<SuggestedPart> suggestedParts =
                suggestedPartRepository.findAllByAppointment_Customer_Id(user.getId());

        List<SuggestPartDto> dtos = new ArrayList<>();

        for (SuggestedPart x : suggestedParts) {
            SuggestPartDto dto = new SuggestPartDto();
            dto.setTotal_price(x.getPart().getUnitPrice() * x.getQuantity());
            dto.setQuantity(x.getQuantity());
            dto.setTechnician_note(x.getTechnicianNote());
            dto.setStatus(x.getStatus());
            dto.setPart_name(x.getPart().getName());
            dto.setUnit_price(x.getPart().getUnitPrice());
            dto.setPart_description(x.getPart().getDescription());
            dto.setPartId(x.getPart().getId());
            dto.setAppointmentId(x.getAppointment().getId());
            dto.setSuggestPart_Id(x.getId());
            dto.setAppointmentDate(x.getAppointment().getAppointmentDate());
            dtos.add(dto);
        }

        return dtos;
    }


    @Transactional
    public RequestSuggestPart createSuggestPart(RequestSuggestPart dto){
        ServiceAppointment serviceAppointment = serviceAppointmentService.findById(dto.getAppointmentId());
        if(serviceAppointment == null){
            throw new NotFoundException("Appointment khong ton tai");
        }
        Part part = partRepository.findById(dto.getPartId()).orElseThrow(() -> new NotFoundException("Part khong ton tai"));;
        if(part == null){
            throw new NotFoundException("Part khong ton tai");
        }
        SuggestedPart suggestedPart = suggestPartMapper.toEntity(dto);
        suggestedPart.setAppointment(serviceAppointment);
        suggestedPart.setPart(part);
        suggestedPart.setStatus("pending");
        suggestedPartRepository.save(suggestedPart);
        return suggestPartMapper.toDto(suggestedPart);
    }

    @Transactional
    public List<RequestSuggestPart> createSuggestParts(List<RequestSuggestPart> requestSuggestParts) {
        RequestSuggestPart requestSuggestPart = requestSuggestParts.get(0);
        if(requestSuggestPart == null){
            throw new NotFoundException("Suggest part khong ton tai");
        }
        List<SuggestedPart> suggestedParts = suggestedPartRepository.findAllByAppointment_Id(requestSuggestPart.getAppointmentId());
        if(!suggestedParts.isEmpty()){
            suggestedPartRepository.deleteAll(suggestedParts);
        }
        List<RequestSuggestPart> created = new ArrayList<>();
        for (RequestSuggestPart dto : requestSuggestParts) {
            created.add(createSuggestPart(dto));
        }
        return created;
    }
}
