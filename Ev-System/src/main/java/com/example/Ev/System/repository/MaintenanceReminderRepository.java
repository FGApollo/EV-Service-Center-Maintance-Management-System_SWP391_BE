package com.example.Ev.System.repository;

import com.example.Ev.System.entity.MaintenanceRecord;
import com.example.Ev.System.entity.MaintenanceReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MaintenanceReminderRepository extends JpaRepository<MaintenanceReminder, Integer> {

    // simpler: custom query
    @Query("SELECT m FROM MaintenanceReminder m WHERE m.isSent = false AND m.reminderDate <= :now")
    List<MaintenanceReminder> findDueReminders(@Param("now") Instant now);

    boolean existsByVehicleIdAndReminderDate(Long vehicleId, Instant reminderDate);

}
