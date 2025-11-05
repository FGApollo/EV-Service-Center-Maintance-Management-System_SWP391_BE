package com.example.Ev.System.controller;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.StaffAssignmentDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.User;
import com.example.Ev.System.mapper.StaffAssignmentMapper;
import com.example.Ev.System.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String role,Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int id = user.getServiceCenter().getId();
        return ResponseEntity.ok(userService.getAllByRole(role,id));
        //test xong
        //lay user theo role va theo thang manager centerId
    }

    @GetMapping("/allTechnicians")
    public ResponseEntity<List<UserDto>> getTechnician(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int id = user.getServiceCenter().getId();
        return ResponseEntity.ok(userService.getAllByRole("technician",id));
        //test xong
        //lay user theo role va theo thang manager centerId
    }

    @PostMapping("/employees")
    public ResponseEntity<UserDto> createEmployee(
            @RequestBody RegisterUserDto userDto,
            @RequestParam String role,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int id = user.getServiceCenter().getId();
        return ResponseEntity.ok(userService.createEmployee(userDto, role,id));
        //test xong
        //tao user theo role va theo thang manager centerId
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




}
