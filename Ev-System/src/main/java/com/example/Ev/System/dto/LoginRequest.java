package com.example.Ev.System.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class LoginRequest {
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email là bắt buộc")
    private String email;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 5, max = 128, message = "Mật khẩu phải từ 5-128 ký tự")
    private String password;


}
