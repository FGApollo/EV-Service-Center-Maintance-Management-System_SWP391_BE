package com.example.Ev.System.repository;


import com.example.Ev.System.entity.Worklog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLogRepository extends JpaRepository<Worklog, Integer> {

}
