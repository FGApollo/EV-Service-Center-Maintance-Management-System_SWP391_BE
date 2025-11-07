package com.example.Ev.System.dto;

import com.example.Ev.System.entity.ServiceCenter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterDTO {
    private Integer centerId;
    private String name;
    private String address;
    private String phone;
    private String email;

    public CenterDTO(ServiceCenter serviceCenter) {
        this.centerId = serviceCenter.getId();
        this.name = serviceCenter.getName();
        this.address = serviceCenter.getAddress();
        this.phone = serviceCenter.getPhone();
        this.email = serviceCenter.getEmail();
    }
}
