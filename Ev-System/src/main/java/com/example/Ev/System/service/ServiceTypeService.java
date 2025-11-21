package com.example.Ev.System.service;

import com.example.Ev.System.dto.ServiceTypeDto;
import com.example.Ev.System.entity.ServiceType;
import com.example.Ev.System.repository.ServiceTypeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }


    public List<ServiceTypeDto> getAllDtos() {
        List<ServiceType> entities = serviceTypeRepository.findAll();
        List<ServiceTypeDto> result = new ArrayList<>(entities.size());

        for (ServiceType s : entities) {
            ServiceTypeDto dto = new ServiceTypeDto();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setDescription(s.getDescription());
            dto.setPrice(s.getPrice());
            dto.setDurationEst(s.getDurationEst());

            result.add(dto);
        }

        return result;
    }


}