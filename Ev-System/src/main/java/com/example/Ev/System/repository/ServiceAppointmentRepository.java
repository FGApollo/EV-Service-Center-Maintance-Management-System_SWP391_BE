package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, Integer> {
    public ServiceAppointment findById(int id);
    List<ServiceAppointment> findByCreatedAtAfter(Instant startDate);
}
