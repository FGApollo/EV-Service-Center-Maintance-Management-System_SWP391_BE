package com.example.Ev.System.repository;

import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Integer> {
    @Query("SELECT sa.staff FROM StaffAssignment sa WHERE sa.appointment.id = :appointmentId")
    List<User> findStaffByAppointmentId(@Param("appointmentId") Integer appointmentId);

    List<StaffAssignment> findStaffAssignmentsByAppointment_Id(Integer appointmentId);

    Optional<StaffAssignment> findByAppointment_IdAndStaff_Id(Integer appointmentId, Integer staffId);

    void deleteByAppointment_Id(Integer appointmentId);

}
