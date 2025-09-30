package com.example.Ev.System.repository;

import com.example.Ev.System.entity.Partyusage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyusageRepository extends JpaRepository<Partyusage, Integer> {
}
