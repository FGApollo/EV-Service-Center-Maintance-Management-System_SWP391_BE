package com.example.Ev.System.repository;

import com.example.Ev.System.entity.AppointmentService;
import com.example.Ev.System.entity.AppointmentServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, AppointmentServiceId> {
}
