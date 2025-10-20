package com.example.Ev.System.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RegisterUserDto {

    @NotBlank(message = "Họ và tên không được để trống.")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự.")
    private String fullName;

    @NotBlank(message = "Email không được để trống.")
//    @Email(message = "Email không hợp lệ.")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống.")
//    @Pattern(
//            regexp = "^(0|\\+84)\\d{9,10}$",
//            message = "Số điện thoại không hợp lệ. Ví dụ: 0987654321 hoặc +84987654321."
//    )
    private String phoneNumber;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Size(min = 8, max = 50, message = "Mật khẩu phải có độ dài từ 8 đến 50 ký tự.")
//    @Pattern(
//            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message = "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt."
//    )
    private String passwordHash;
}
