package com.example.Ev.System.repository;

import com.example.Ev.System.entity.User;
import com.example.Ev.System.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    Vehicle findVehicleById(Integer id);

    List<Vehicle> findVehicleByCustomerAndDeleted(User customer, boolean deleted);

    List<Vehicle> findVehicleByCustomer(User customer);
}
