package com.example.Ev.System.repository;

import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.PartUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PartUsageRepository extends JpaRepository<PartUsage, Integer> {
    List<PartUsage> findByRecord_StartTimeBetween(Instant startTime, Instant endTime);
    List<PartUsage> findByRecord(MaintenanceRecord record);
}
