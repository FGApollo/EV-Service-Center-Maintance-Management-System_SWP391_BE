package com.example.Ev.System.service;

import com.example.Ev.System.dto.CenterDTO;
import com.example.Ev.System.entity.ServiceCenter;
import com.example.Ev.System.exception.NotFoundException;
import com.example.Ev.System.repository.ServiceCenterRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CenterService {
    ServiceCenterRepository serviceCenterRepository;

    public CenterService(ServiceCenterRepository serviceCenterRepository) {
        this.serviceCenterRepository = serviceCenterRepository;
    }

    public List<CenterDTO> GetAllCenter() {
        List<CenterDTO> centerDTOS = new ArrayList<>();
        List<ServiceCenter> serviceCenters = serviceCenterRepository.findAll();

        CenterDTO centerDTO = null;
        for (ServiceCenter x : serviceCenters) {
            centerDTO = new CenterDTO();
            centerDTO.setCenterId(x.getId());
            centerDTO.setPhone(x.getPhone());
            centerDTO.setEmail(x.getEmail());
            centerDTO.setName(x.getName());
            centerDTO.setAddress(x.getAddress());
            centerDTOS.add(centerDTO);
        }



        return centerDTOS;
    }

    public CenterDTO CreateCenter(CenterDTO dto){
        ServiceCenter serviceCenter = new ServiceCenter();
        serviceCenter.setAddress(dto.getAddress());
        serviceCenter.setName(dto.getName());
        serviceCenter.setEmail(dto.getEmail());
        serviceCenter.setPhone(dto.getPhone());
        serviceCenter.setStatus("active");
        serviceCenterRepository.save(serviceCenter);

//        CenterDTO centerDTO = new CenterDTO();
//        centerDTO.setCenterId(serviceCenter.getId());
//        centerDTO.setPhone(serviceCenter.getPhone());
//        centerDTO.setEmail(serviceCenter.getEmail());
//        centerDTO.setName(serviceCenter.getName());
//        centerDTO.setAddress(serviceCenter.getAddress());

        return  new CenterDTO(serviceCenter);
    }

//    public CenterDTO UpdateCenter(CenterDTO centerDTO, Integer id){
//        ServiceCenter serviceCenter = serviceCenterRepository.findServiceCenterById(id);
//        if(serviceCenter == null){
//            throw new NotFoundException("Center not found with id:"  + id);
//        }
//
//        serviceCenter.setAddress(centerDTO.getAddress());
//        serviceCenter.setName(centerDTO.getName());
//        serviceCenter.setEmail(centerDTO.getEmail());
//        serviceCenter.setPhone(centerDTO.getPhone());
//
//        serviceCenterRepository.save(serviceCenter);
//
//        CenterDTO dto = new CenterDTO();
//        dto.setCenterId(serviceCenter.getId());
//        dto.setPhone(serviceCenter.getPhone());
//        dto.setEmail(serviceCenter.getEmail());
//        dto.setName(serviceCenter.getName());
//        dto.setAddress(serviceCenter.getAddress());
//
//        return new CenterDTO(serviceCenter);
//    }

    public CenterDTO UpdateCenter(CenterDTO centerDTO, Integer id){
        ServiceCenter serviceCenter = serviceCenterRepository.findServiceCenterById(id);
        if(serviceCenter == null){
            throw new NotFoundException("Center not found with id:"  + id);
        }

        if (centerDTO.getName() != null && !centerDTO.getName().isBlank()) {
            serviceCenter.setName(centerDTO.getName());
        }

        if (centerDTO.getEmail() != null && !centerDTO.getEmail().isBlank()) {
            serviceCenter.setEmail(centerDTO.getEmail());
        }

        if (centerDTO.getPhone() != null && !centerDTO.getPhone().isBlank()) {
            serviceCenter.setPhone(centerDTO.getPhone());
        }

        if (centerDTO.getAddress() != null && !centerDTO.getAddress().isBlank()) {
            serviceCenter.setAddress(centerDTO.getAddress());
        }

        serviceCenterRepository.save(serviceCenter);


        return new CenterDTO(serviceCenter);
    }

    public void DeleteCenter(Integer id){
        ServiceCenter serviceCenter = serviceCenterRepository.findServiceCenterById(id);
        if(serviceCenter == null){
            throw new NotFoundException("Center not found with id:"  + id);
        }

        serviceCenter.setStatus("inactive");
        serviceCenterRepository.save(serviceCenter);

    }
}
