package com.example.Ev.System.repository;

import com.example.Ev.System.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PartRepository extends JpaRepository<Part,Long> {
}
