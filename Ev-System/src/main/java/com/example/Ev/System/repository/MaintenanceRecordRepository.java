package com.example.Ev.System.repository;

import com.example.Ev.System.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord,Long> {
}
