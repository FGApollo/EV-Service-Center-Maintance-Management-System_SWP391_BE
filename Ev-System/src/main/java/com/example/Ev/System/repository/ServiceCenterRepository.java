package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Integer> {
    public ServiceCenter findServiceCenterById(Integer id);

    public List<ServiceCenter> findAll();
}
