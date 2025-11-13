package com.example.Ev.System.service;

import com.example.Ev.System.dto.StaffAssignmentDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.ServiceCenter;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.StaffAssignmentMapper;
import com.example.Ev.System.repository.AppointmentRepository;
import com.example.Ev.System.repository.ServiceCenterRepository;
import com.example.Ev.System.repository.StaffAssignmentRepository;
import com.example.Ev.System.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private final StaffAssignmentMapper staffAssignmentMapper;


    public StaffAppointmentService(AppointmentRepository appointmentRepository, StaffAssignmentRepository staffAssignmentRepository, UserRepository userRepository, ServiceCenterRepository serviceCenterRepository, StaffAssignmentMapper staffAssignmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
        this.userRepository = userRepository;
        this.serviceCenterRepository = serviceCenterRepository;
        this.staffAssignmentMapper = staffAssignmentMapper;
    }

    @Transactional
    public List<StaffAssignment> assignTechnicians(Integer appointmentId, List<Integer> staffIds,String note , Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        Integer centerId = currentUser.getServiceCenter().getId();

        ServiceAppointment appointmentCheck = appointmentRepository.findById(appointmentId).orElse(null);

        if (appointmentCheck == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }

        if (!appointmentCheck.getServiceCenter().getId().equals(centerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Appointment not in your center");
        }

        if (staffIds == null || staffIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff list cannot be empty");
        }

        for (Integer staffId : staffIds) {
            User user = userRepository.findById(staffId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found: " + staffId));

            // Check service center match
            if (user.getServiceCenter() == null || !user.getServiceCenter().getId().equals(centerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Staff not in your center (" + staffId + ")");
            }

            if (!"technician".equalsIgnoreCase(user.getRole())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a technician: " + staffId);
            }
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


    public void  autoAssignTechnician(Integer appointmentId , String note , int serviceCenterId,String status , Authentication authentication) {
        List<User> freeTech = getFreeTechnician(serviceCenterId,status);
        List<Integer> chosenTech = new ArrayList<>();
        chosenTech.add(freeTech.get(0).getId());
        assignTechnicians(appointmentId, chosenTech , note , authentication);
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

    public List<StaffAssignmentDto> getStaffAsignment(Authentication authentication)
    {
        List<StaffAssignmentDto> staffAssignmentDtos = new ArrayList<>();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        List<User> allTechs = userRepository.findAllByRoleAndServiceCenter("technician", user.getServiceCenter())
                .stream()
                .filter(tech -> "active".equalsIgnoreCase(tech.getStatus()))
                .toList();;
        for(User tech : allTechs){
            StaffAssignmentDto dto = staffAssignmentMapper.toDto(tech);
            if(getFreeTechnician(user.getServiceCenter().getId(),"in_progress").contains(tech)){
                dto.setWorking(false);
                staffAssignmentDtos.add(dto);
            } else {
                dto.setWorking(true);
                List<ServiceAppointment> appointments = getAppointmentsByStaffId(tech.getId());
                if(!appointments.isEmpty()){
                    for(ServiceAppointment appointment : appointments){
                        String appointmentIds = appointments.stream()
                                .map(a -> a.getId().toString())
                                .collect(Collectors.joining(","));
                        dto.setAppointmentId(appointmentIds);
                    }
                }
                staffAssignmentDtos.add(dto);
            }

        }
        return  staffAssignmentDtos;
    }

    public List<ServiceAppointment> getAppointmentsByStaffId(Integer staffId) {
        List<ServiceAppointment> appointments = appointmentRepository.findAllByStaffAssignments_staff_id(staffId);
        return appointments;
    }

    public List<Integer> staffIdsByAppointmentId(Integer appointmentId) {
        List<StaffAssignment> staffAssignments = staffAssignmentRepository.findStaffAssignmentsByAppointment_Id(appointmentId);
        List<Integer> staffIds = new ArrayList<>();
        for(StaffAssignment staffAssignment : staffAssignments){
            staffIds.add(staffAssignment.getStaff().getId());
        }
        return  staffIds;
    }

    @Transactional
    public List<StaffAssignmentDto> assignTechniciansDto(Integer appointmentId, List<Integer> staffIds, String note, Authentication auth) {
        List<StaffAssignment> assignments = assignTechnicians(appointmentId, staffIds, note, auth);
        return assignments.stream()
                .map(a -> {
                    StaffAssignmentDto dto = staffAssignmentMapper.toDtoWithStatus(a.getStaff(), true);
                    dto.setAppointmentId(String.valueOf(appointmentId));
                    return dto;
                })
                .toList();
    }




//    public List<StaffAssignmentDto> getTechniciansWithStatus(String status , Authentication authentication) {
//        String email = authentication.getName();
//        User user = userRepository.findByEmail(email).orElse(null);
//        int id = user.getServiceCenter().getId();
//        Set<User> busyTechs = new HashSet<>();
//
//        List<ServiceAppointment> appointments = appointmentRepository.findAllByStatusAndServiceCenter(status,id);
//        for (ServiceAppointment appointment : appointments) {
//            staffAssignmentRepository.findStaffByAppointmentId(appointment.getId())
//                    .forEach(busyTechs::add);
//        }
//
//        List<User> allTechs = userRepository.findAllByRoleAndServiceCenter("technician", id);
//
//        // Map all with their current working status
//        return allTechs.stream()
//                .map(tech -> StaffAssignmentMapper.toDtoWithStatus(tech, busyTechs.contains(tech)))
//                .toList();
//    }





}
