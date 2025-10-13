package com.example.Ev.System.repository;

import com.example.Ev.System.entity.AppointmentService;
import com.example.Ev.System.entity.AppointmentServiceId;
import com.example.Ev.System.entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<ServiceAppointment, Integer> {
    boolean existsByServiceCenter_IdAndAppointmentDate(Long centerId, Instant appointmentDate);
}


