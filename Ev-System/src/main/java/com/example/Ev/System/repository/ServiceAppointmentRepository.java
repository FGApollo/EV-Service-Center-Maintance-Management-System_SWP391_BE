package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, Integer> {    public ServiceAppointment findById(int id);
    List<ServiceAppointment> findByCreatedAtAfter(Instant startDate);

    @Query("select a from ServiceAppointment a left join fetch a.serviceTypes where a.id = :id")
    Optional<ServiceAppointment> findByIdWithServiceTypes(@Param("id") Integer id);
}
