package com.example.Ev.System.repository;

import com.example.Ev.System.entity.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Integer> {
    List<AppointmentService> findByAppointmentId(Integer appointmentId);
}
