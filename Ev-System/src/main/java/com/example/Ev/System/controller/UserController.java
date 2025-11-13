package com.example.Ev.System.controller;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.StaffAssignmentDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.StaffAssignment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.StaffAssignmentMapper;
import com.example.Ev.System.service.StaffAppointmentService;
import com.example.Ev.System.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final StaffAssignmentMapper staffAssignmentMapper;
    private final StaffAppointmentService staffAppointmentService;
    public UserController(UserService userService, StaffAssignmentMapper staffAssignmentMapper, StaffAppointmentService staffAppointmentService) {
        this.userService = userService;
        this.staffAssignmentMapper = staffAssignmentMapper;
        this.staffAppointmentService = staffAppointmentService;
    }

//    @GetMapping("")
//    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String role,Authentication authentication) {
//        String email = authentication.getName();
//        User user = userService.getUserByEmail(email);
//        int id = user.getServiceCenter().getId();
//        return ResponseEntity.ok(userService.getAllByRole(role,id));
//        //test xong
//        //lay user theo role va theo thang manager centerId
//    }

    @GetMapping("/all/{role}")
    @PreAuthorize("(hasAuthority('admin'))")
    public ResponseEntity<List<UserDto>> getAllUsersByRole(@PathVariable String role) {
        List<UserDto> users = userService.getAllUserByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/allTechnicians")
    @Transactional
    public ResponseEntity<List<StaffAssignmentDto>> getTechnician(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int id = user.getServiceCenter().getId();
        List<StaffAssignmentDto> staffAssignmentList = staffAppointmentService.getStaffAsignment(authentication);
        return ResponseEntity.ok(staffAssignmentList);
        //test xong
        //lay user theo role va theo thang manager centerId
    }

//    @PostMapping("/employees")
//    public ResponseEntity<UserDto> createEmployee(
//            @RequestBody RegisterUserDto userDto,
//            @RequestParam String role,
//            Authentication authentication) {
//        String email = authentication.getName();
//        User user = userService.getUserByEmail(email);
//        int id = user.getServiceCenter().getId();
//        return ResponseEntity.ok(userService.createEmployee(userDto, role,id));
//        //test xong
//        //tao user theo role va theo thang manager centerId
//    }

    @PostMapping(value = "/employees", consumes = {"multipart/form-data"})
    public ResponseEntity<UserDto> createEmployee(
            @RequestPart("user") RegisterUserDto userDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam String role,
            Authentication authentication) throws IOException {

        String email = authentication.getName();
        User manager = userService.getUserByEmail(email);
        int centerId = manager.getServiceCenter().getId();

        UserDto createdEmployee = userService.createEmployee(userDto, role, centerId, file);
        return ResponseEntity.ok(createdEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteEmployee(@PathVariable("id") Integer id) {
        UserDto userDto = userService.deleteAccount(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/all_customer")
    public ResponseEntity<List<UserDto>> getAllCustomer(){
        List<UserDto> customers = userService.getAllCustomer("customer");
        return ResponseEntity.ok(customers);
    }

    @PreAuthorize("(hasAuthority('manager'))")
    @GetMapping("/center/staff_and_technician")
    public ResponseEntity<List<UserDto>> getStaffAndTechnician(Authentication authentication){
        String email = authentication.getName();
        List<UserDto> user = userService.getStaffAndTechnicianInSpecificCenter(email);
        return ResponseEntity.ok(user);
    }

}
