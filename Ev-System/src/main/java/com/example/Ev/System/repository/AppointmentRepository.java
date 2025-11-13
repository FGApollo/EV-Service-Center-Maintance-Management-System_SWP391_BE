package com.example.Ev.System.repository;

import com.example.Ev.System.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<ServiceAppointment, Integer> {
    boolean existsByServiceCenter_IdAndAppointmentDate(Long centerId, Instant appointmentDate);
    List<ServiceAppointment> findByCustomerOrderByAppointmentDateDesc(User customer);

    List<ServiceAppointment> findAllByStatusAndServiceCenter(String status, ServiceCenter serviceCenter);
    public List<ServiceAppointment> findAllByStaffAssignments_staff_id(Integer staffId);

    List<ServiceAppointment> findAllByCustomer(User customer);

    public ServiceAppointment findFirstByVehicleOrderByCreatedAtDesc(Vehicle vehicle);

    List<ServiceAppointment> findAllByVehicle(Vehicle vehicle);

    @Query("""
    SELECT a FROM ServiceAppointment a
    LEFT JOIN FETCH a.serviceTypes
    LEFT JOIN FETCH a.customer
    LEFT JOIN FETCH a.vehicle
    WHERE a.id = :id
""")
    Optional<ServiceAppointment> findByIdWithDetails(@Param("id") Integer id); //giup load tranh lazy loading

    @Query("SELECT DISTINCT sa.vehicle FROM ServiceAppointment sa WHERE sa.status = :status")
    List<Vehicle> findDistincVehicleByStatus(@Param("status") String status);

    List<ServiceAppointment> findAll();

    List<ServiceAppointment> findAllByServiceCenter(ServiceCenter serviceCenter);

    List<ServiceAppointment> findAllByServiceCenter_Id(Integer serviceCenterId);

}


