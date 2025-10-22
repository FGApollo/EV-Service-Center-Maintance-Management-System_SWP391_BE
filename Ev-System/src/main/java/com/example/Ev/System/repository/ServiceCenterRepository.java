package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Integer> {
    Optional<ServiceCenter> findById(Integer id);
}
