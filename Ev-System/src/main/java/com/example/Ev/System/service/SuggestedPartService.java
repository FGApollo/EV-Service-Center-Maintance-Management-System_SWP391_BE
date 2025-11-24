package com.example.Ev.System.service;

import com.example.Ev.System.dto.SuggestedPartDto;
import com.example.Ev.System.entity.SuggestedPart;
import com.example.Ev.System.repository.SuggestedPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestedPartService implements SuggestedPartServiceI {
    @Autowired
    private SuggestedPartRepository suggestedPartRepository;

    @Override
    public List<SuggestedPartDto> getAllSuggestedPartsByAppointmentId(Integer appointmentId) {
        return suggestedPartRepository.findAllByAppointmentId(appointmentId).stream()
                .map(suggestedPart -> new SuggestedPartDto(
                        suggestedPart.getPart().getId(),
                        suggestedPart.getPart().getName(),
                        suggestedPart.getPart().getDescription(),
                        suggestedPart.getPart().getUnitPrice(),
                        suggestedPart.getQuantity(),
                        suggestedPart.getPart().getUnitPrice()*suggestedPart.getQuantity()
                ))
                .toList();
    }

    @Override
    public SuggestedPart getSuggestedPartById(Integer id) {
        return suggestedPartRepository.findById(id).get();
    }
}
