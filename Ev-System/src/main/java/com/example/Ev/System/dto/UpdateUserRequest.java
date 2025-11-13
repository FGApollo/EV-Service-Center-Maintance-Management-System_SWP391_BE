package com.example.Ev.System.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "Họ và tên phải từ 2 đến 50 ký tự")
    private String fullName;

    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @Pattern(
            regexp = "^(0|\\+84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$",
            message = "Số điện thoại không hợp lệ (phải là số Việt Nam)"
    )
    private String phone;
    private String password;
}
