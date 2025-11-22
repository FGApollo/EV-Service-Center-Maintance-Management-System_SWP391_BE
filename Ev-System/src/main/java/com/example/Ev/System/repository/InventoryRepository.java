package com.example.Ev.System.repository;

import com.example.Ev.System.entity.Inventory;
import com.example.Ev.System.entity.Part;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByCenterIdAndPart(Integer centerId, Part part);

}
