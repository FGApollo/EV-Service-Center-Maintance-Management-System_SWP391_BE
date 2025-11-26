package com.example.Ev.System.repository;

import com.example.Ev.System.entity.SuggestedPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuggestedPartRepository extends JpaRepository<SuggestedPart, Long> {
    List<SuggestedPart> findAllByAppointment_Id(Integer appointmentId);

    SuggestedPart findById(Integer id);

    List<SuggestedPart> findAllByAppointment_Customer_IdOrderByIdDesc(Integer customerId);
}
