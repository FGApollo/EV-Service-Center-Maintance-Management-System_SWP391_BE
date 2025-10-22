package com.example.Ev.System.repository;

import com.example.Ev.System.entity.PartUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartUsageRepository extends JpaRepository<PartUsage, Integer> {
}
