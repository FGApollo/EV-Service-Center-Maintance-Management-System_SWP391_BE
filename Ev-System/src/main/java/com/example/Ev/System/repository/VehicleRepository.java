package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceCenter;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findById(Integer id);
}
