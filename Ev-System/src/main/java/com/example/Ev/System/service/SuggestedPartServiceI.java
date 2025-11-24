package com.example.Ev.System.service;

import com.example.Ev.System.dto.SuggestedPartDto;
import com.example.Ev.System.entity.SuggestedPart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SuggestedPartServiceI {
    List<SuggestedPartDto> getAllSuggestedPartsByAppointmentId(Integer appointmentId);
    SuggestedPart getSuggestedPartById(Integer id);
}
