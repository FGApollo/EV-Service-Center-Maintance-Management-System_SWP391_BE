package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<ServiceAppointment,Integer> {
    public List<ServiceAppointment> findAllByStatus(String status);
    public List<ServiceAppointment> findAllByStaff_Id(Integer staffId);
}
