package com.example.Ev.System.repository;

import com.example.Ev.System.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Integer> {
    List<MaintenanceRecord> findByEndTimeIsNotNull();
}
