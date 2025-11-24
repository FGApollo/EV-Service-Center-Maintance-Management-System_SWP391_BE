package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {
//    List<ServiceType> findByAppointmentId(Integer appointmentId);
}
