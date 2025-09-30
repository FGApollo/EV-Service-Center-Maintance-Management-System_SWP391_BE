package com.example.Ev.System.service;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.StaffAssignmentRepository;
import com.example.Ev.System.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StaffAppointmentService {
    private  final AppointmentRepository appointmentRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final UserRepository userRepository;

    public StaffAppointmentService(AppointmentRepository appointmentRepository, StaffAssignmentRepository staffAssignmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
        this.userRepository = userRepository;
    }

    public List<StaffAssignment> assignTechnicians(Integer appointmentId, List<Integer> staffIds, String role, String note) {
        if (staffIds == null || staffIds.isEmpty()) {
            return List.of();
        }
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        List<StaffAssignment> assignments = new ArrayList<>();
        for (Integer staffId : staffIds) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));
            if (!staff.getRole().equals(role)) {
                throw new RuntimeException("User " + staff.getFullName() + " is not a " + role);
            }
            StaffAssignment staffAssignment = new StaffAssignment();
            staffAssignment.setAppointment(appointment);
            staffAssignment.setStaff(staff);
            staffAssignment.setRole(role);
            staffAssignment.setStartTime(Instant.now());
            staffAssignment.setNotes(note);
            assignments.add(staffAssignmentRepository.save(staffAssignment));
        }

        return assignments;
    }


    public void  autoAssignTechnician(Integer appointmentId  , String role , String note){
        List<User> freeTech = getFreeTechnician();
        List<Integer> chosenTech = new ArrayList<>();
        chosenTech.add(freeTech.get(0).getId());
        assignTechnicians(appointmentId, chosenTech, role, note);
    }

    public List<User> getFreeTechnician(){
        Set<User> busyTech = new HashSet<>();
        List<ServiceAppointment> appointments = appointmentRepository.findAllByStatus("progress");
        for(ServiceAppointment appointment : appointments){
            staffAssignmentRepository.findStaffByAppointmentId(appointment.getId()).forEach(busyTech::add);
        }
        List<User> allTech = userRepository.findAllByRole("technician");
        return allTech.stream()
                .filter(tech -> !busyTech.contains(tech))
                .toList();
    }

}
