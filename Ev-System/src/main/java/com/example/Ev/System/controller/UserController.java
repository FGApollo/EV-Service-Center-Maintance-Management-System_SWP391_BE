package com.example.Ev.System.controller;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.service.UserService;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Controller
@RequestMapping("/Users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;

    }
    @PostMapping("")
    public ResponseEntity<UserDto> register(@RequestBody RegisterUserDto registerUserDto,
                                            UriComponentsBuilder uriComponentsBuilder)
    {
        UserDto userDTO = userService.createUser(registerUserDto,uriComponentsBuilder);
        var uri = uriComponentsBuilder.path("/Users/{id}").buildAndExpand(userDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(userDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String role) {
        return ResponseEntity.ok(userService.getAllByRole(role));
    }

    @PostMapping("/employees")
    public ResponseEntity<UserDto> createEmployee(
            @RequestBody UserDto userDto,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.createEmployee(userDto, role));
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<UserDto> deleteEmployee(@PathVariable("id") Integer id) {
        UserDto userDto = userService.deleteAccount(id);
        return ResponseEntity.ok(userDto);
    }


}
