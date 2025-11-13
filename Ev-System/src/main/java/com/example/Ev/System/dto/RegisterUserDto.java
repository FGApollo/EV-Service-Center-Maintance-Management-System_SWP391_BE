package com.example.Ev.System.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ và tên phải từ 2 đến 50 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @NotBlank(message = "số điện thoại không được để trống")
    @Pattern(regexp = "^(032|033|034|035|036|037|038|039" +
            "|086|096|097|098" +
            "|083|084|085|081|082|088" +
            "|0123|0124|0125|0127|0128|0129" +
            "|070|079|077|076|078" +
            "|0120|0121|0122|0126|0128" +
            "|056|058" +
            "|0186|0188" +
            "|059" +
            "|0199)\\d{7}$",
            message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, max = 20, message = "Mật khẩu phải từ 5 đến 20 ký tự")
    private String password;

    private String certificateLink;
}
