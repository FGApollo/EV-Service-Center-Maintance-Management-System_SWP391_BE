package com.example.Ev.System.service;

import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.ServiceCenter;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.ServiceCenterRepository;
import com.example.Ev.System.repository.StaffAssignmentRepository;
import com.example.Ev.System.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.beans.Customizer;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffAppointmentService {
    private  final AppointmentRepository appointmentRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final UserRepository userRepository;
    private final ServiceCenterRepository serviceCenterRepository;

    public StaffAppointmentService(AppointmentRepository appointmentRepository, StaffAssignmentRepository staffAssignmentRepository, UserRepository userRepository, ServiceCenterRepository serviceCenterRepository) {
        this.appointmentRepository = appointmentRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
        this.userRepository = userRepository;
        this.serviceCenterRepository = serviceCenterRepository;
    }

    @Transactional
    public List<StaffAssignment> assignTechnicians(Integer appointmentId, List<Integer> staffIds,String note) {
        if (staffIds == null || staffIds.isEmpty()) {
            return List.of();
        }
        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if(appointment != null) {
            staffAssignmentRepository.deleteByAppointment_Id(appointmentId);
        }
        List<StaffAssignment> assignments = new ArrayList<>();
        for (Integer staffId : staffIds) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));
            StaffAssignment staffAssignment = new StaffAssignment();
            staffAssignment.setAppointment(appointment);
            staffAssignment.setStaff(staff);
            staffAssignment.setRole("technician");
            staffAssignment.setStartTime(appointment.getAppointmentDate());
            staffAssignment.setNotes(note);
            assignments.add(staffAssignmentRepository.save(staffAssignment));
        }

        return assignments;
    }


    public void  autoAssignTechnician(Integer appointmentId , String note , int serviceCenterId,String status){
        List<User> freeTech = getFreeTechnician(serviceCenterId,status);
        List<Integer> chosenTech = new ArrayList<>();
        chosenTech.add(freeTech.get(0).getId());
        assignTechnicians(appointmentId, chosenTech , note);
    }


    public List<User> getFreeTechnician(int id,String status){
        ServiceCenter serviceCenter = serviceCenterRepository.findServiceCenterById(id);
        Set<User> busyTech = new HashSet<>();
        List<ServiceAppointment> appointments = appointmentRepository.findAllByStatusAndServiceCenter(status,serviceCenter);
        List<User> employeeCenter = userRepository.findAllByServiceCenter(serviceCenter);
        for(ServiceAppointment appointment : appointments){
            staffAssignmentRepository.findStaffByAppointmentId(appointment.getId()).forEach(busyTech::add);
        }
        List<User> allTech = userRepository.findAllByRoleAndServiceCenter("technician",serviceCenter);
        return allTech.stream()
                .filter(tech -> !busyTech.contains(tech))
                .toList();
    }





}
