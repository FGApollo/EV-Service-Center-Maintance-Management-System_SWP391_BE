package com.example.Ev.System.repository;

import com.example.Ev.System.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<ServiceAppointment, Integer> {
    boolean existsByServiceCenter_IdAndAppointmentDate(Long centerId, Instant appointmentDate);
    List<ServiceAppointment> findByCustomerOrderByAppointmentDateDesc(User customer);
    public List<ServiceAppointment> findAllByStatus(String status);
    public List<ServiceAppointment> findAllByStaffAssignments_staff_id(Integer staffId);

    List<ServiceAppointment> findAllByCustomer(User customer);

    public ServiceAppointment findFirstByVehicleOrderByCreatedAtDesc(Vehicle vehicle);

    List<ServiceAppointment> findAllByVehicle(Vehicle vehicle);

    

}


