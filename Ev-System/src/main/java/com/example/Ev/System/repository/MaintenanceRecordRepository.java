package com.example.Ev.System.repository;

import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


import java.util.List;

@Repository

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Integer> {

    // Find maintenance records where the technicianIds string contains the given ID
    @Query("SELECT m FROM MaintenanceRecord m WHERE m.technicianIds LIKE %:technicianId%")
    Optional<List<MaintenanceRecord>> findByTechnicianId(@Param("technicianId") String technicianId);

    Optional<MaintenanceRecord> findFirstByAppointment_Id(Integer appointmentId);

    Optional<MaintenanceRecord> findFirstByAppointment_IdOrderByIdDesc(Integer appointmentId); //Sau nay dung cai nay


    List<MaintenanceRecord> findByAppointment_Id(Integer appointmentId);

    void deleteByAppointment_Id(Integer appointmentId);


}

