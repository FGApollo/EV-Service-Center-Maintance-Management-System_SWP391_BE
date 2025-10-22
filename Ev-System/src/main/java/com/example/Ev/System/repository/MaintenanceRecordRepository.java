package com.example.Ev.System.repository;

import com.example.Ev.System.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    // Find maintenance records where the technicianIds string contains the given ID
    @Query("SELECT m FROM MaintenanceRecord m WHERE m.technicianIds LIKE %:technicianId%")
    Optional<List<MaintenanceRecord>> findByTechnicianId(@Param("technicianId") String technicianId);

    Optional<MaintenanceRecord> findFirstByAppointment_Id(Integer appointmentId);

    List<MaintenanceRecord> findByAppointment_Id(Integer appointmentId);

    void deleteByAppointment_Id(Integer appointmentId);
}
