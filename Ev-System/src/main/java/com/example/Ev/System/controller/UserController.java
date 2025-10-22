package com.example.Ev.System.controller;
import com.example.Ev.System.dto.RegisterUserDto;
import com.example.Ev.System.dto.UserDto;
import com.example.Ev.System.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
@RequestMapping("/api/auth/register")
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
}
