package com.example.Ev.System.repository;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.SuggestedPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestedPartRepository extends JpaRepository<SuggestedPart, Integer> {
    List<SuggestedPart> findAllByAppointmentId(Integer appointmentId);
}
