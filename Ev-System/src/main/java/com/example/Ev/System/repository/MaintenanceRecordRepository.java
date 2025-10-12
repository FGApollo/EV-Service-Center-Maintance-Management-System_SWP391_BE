package com.example.Ev.System.repository;

import com.example.Ev.System.dto.MaintainanceRecordDto;
import com.example.Ev.System.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord,Long> {
    Optional<MaintenanceRecord> getMaintenanceRecordByTechnician_Id(Integer technicianId);
}
