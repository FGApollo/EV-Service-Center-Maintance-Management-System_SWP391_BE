package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<ServiceAppointment,Integer> {
    public List<ServiceAppointment> findAllByStatus(String status);
    public List<ServiceAppointment> findAllByStaffAssignments_staff_id(Integer staffId);
    boolean existsByServiceCenter_IdAndAppointmentDate(Long centerId, Instant appointmentDate);

    List<ServiceAppointment> findByCustomerOrderByAppointmentDateDesc(User customer);
}
