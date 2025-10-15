package com.example.Ev.System.repository;

import com.example.Ev.System.entity.InventoryEntity;
import com.example.Ev.System.entity.PartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    Optional<InventoryEntity> findByCenterIdAndPart(Integer centerId, PartEntity partEntity);
}
