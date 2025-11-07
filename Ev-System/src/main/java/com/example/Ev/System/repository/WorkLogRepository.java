package com.example.Ev.System.repository;


import com.example.Ev.System.entity.Worklog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkLogRepository extends JpaRepository<Worklog, Integer> {
    List<Worklog> findWorklogsByAppointment_ServiceCenter_Id(Integer serviceCenterId);
}
