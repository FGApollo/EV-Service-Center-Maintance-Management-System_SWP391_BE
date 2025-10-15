package com.example.Ev.System.repository;

import com.example.Ev.System.entity.PartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<PartEntity, Integer> {
}
