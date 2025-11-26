package com.example.Ev.System.repository;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByCenterIdAndPart(Integer centerId, Part part);
    List<Inventory> findByCenterId(Integer centerId);
    void deleteByPart(Part part);
    Inventory findByCenterIdAndPart_Id(Integer centerId, Integer partId);
}
