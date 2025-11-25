package com.example.Ev.System.mapper;

import com.example.Ev.System.dto.RequestSuggestPart;
import com.example.Ev.System.dto.SuggestPartDto;
import com.example.Ev.System.dto.SuggestedPartDto;
import com.example.Ev.System.entity.SuggestedPart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SuggestPartMapper {
    SuggestedPart toEntity(RequestSuggestPart suggestedPartDto);

    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "part.id", target = "partId")
    RequestSuggestPart toDto(SuggestedPart suggestedPart);
}
