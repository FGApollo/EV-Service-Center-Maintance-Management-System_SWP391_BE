package com.example.Ev.System.repository;


import com.example.Ev.System.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLogRepository extends JpaRepository<WorkLog, Integer> {
}
