package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Service;

@Service
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {
    public ServiceType findTopByOrderByDurationEst();
}
