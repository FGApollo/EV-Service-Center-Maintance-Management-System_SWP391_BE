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

    public StaffAssignment assignTechnician(Integer appointmentId , List<Long> staffIds , String role , String note){
        if(staffIds.isEmpty()){
            return null;
        }
        StaffAssignment staffAssignment = new StaffAssignment();
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if(appointment != null){
            staffAssignment.setAppointment(appointment);
        }
        for(Long staffId : staffIds){
            User staff = userRepository.findById(staffId).orElse(null);
            if(staff.getRole().equals(role)){
                staffAssignment.setStaff(staff);
            }
        }
        staffAssignment.setStartTime(Instant.now());
        staffAssignment.setNotes(note);
        return staffAssignmentRepository.save(staffAssignment);
    }

    public void  autoAssignTechnician(Integer appointmentId  , String role , String note){
        List<User> freeTech = getFreeTechnician();
        List<Long> chosenTech = new ArrayList<>();
        chosenTech.add(freeTech.get(0).getId());
        assignTechnician(appointmentId, chosenTech, role, note);
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
