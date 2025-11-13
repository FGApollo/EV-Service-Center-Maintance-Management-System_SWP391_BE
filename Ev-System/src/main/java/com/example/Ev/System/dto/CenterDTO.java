package com.example.Ev.System.dto;

import com.example.Ev.System.entity.ServiceCenter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.Ev.System.validation.ValidationGroups.Create;
import com.example.Ev.System.validation.ValidationGroups.Update;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterDTO {

    private Integer centerId;


    @Size(max = 100, message = "center name tối đa 100 kí tự")
    private String name;


    @Size(max = 100, message = "address tối đa 100 kí tự")
    private String address;


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
            message = "Số điện thoại không hợp lệ", groups = {Create.class, Update.class})
    private String phone;

    @Email(message = "Email không hợp lệ", groups = {Create.class, Update.class})
    @NotBlank(message = "email không được trống", groups = Create.class)
    private String email;

    public CenterDTO(ServiceCenter serviceCenter) {
        this.centerId = serviceCenter.getId();
        this.name = serviceCenter.getName();
        this.address = serviceCenter.getAddress();
        this.phone = serviceCenter.getPhone();
        this.email = serviceCenter.getEmail();
    }
}
