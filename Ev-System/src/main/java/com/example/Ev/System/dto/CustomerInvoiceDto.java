package com.example.Ev.System.dto;

import com.example.Ev.System.entity.PartUsage;
import com.example.Ev.System.entity.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomerInvoiceDto {
    private Integer appointmentId;
    private String customerName;
    private String vehicleModel;
    private List<ServiceType> services;
    private List<PartUsage> parts;
}
