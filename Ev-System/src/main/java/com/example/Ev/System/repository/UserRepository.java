package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceCenter;
import com.example.Ev.System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    Optional<User> findByRole(String role);

    List<User> findAllByRoleAndServiceCenter(String role, ServiceCenter serviceCenter);

    List<User> findAllByServiceCenter(ServiceCenter serviceCenter);

    boolean existsByEmail(String email);

}
