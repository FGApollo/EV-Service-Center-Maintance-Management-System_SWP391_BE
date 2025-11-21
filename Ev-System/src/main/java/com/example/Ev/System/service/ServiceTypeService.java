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

    public ServiceTypeDto create(ServiceTypeDto dto) {
        ServiceType entity = new ServiceType();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setDurationEst(dto.getDurationEst());

        ServiceType saved = serviceTypeRepository.save(entity);
        dto.setId(saved.getId());
        return dto;
    }

    public ServiceTypeDto update(Integer id, ServiceTypeDto dto) {
        ServiceType entity = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceType not found with id " + id));

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setDurationEst(dto.getDurationEst());

        ServiceType updated = serviceTypeRepository.save(entity);
        dto.setId(updated.getId());
        return dto;
    }


    public void delete(Integer id) {
        if (!serviceTypeRepository.existsById(id)) {
            throw new RuntimeException("ServiceType not found with id " + id);
        }
        serviceTypeRepository.deleteById(id);
    }


}