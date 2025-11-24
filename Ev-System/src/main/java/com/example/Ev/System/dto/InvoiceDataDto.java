package com.example.Ev.System.dto;

import com.example.Ev.System.entity.PartUsage;
import com.example.Ev.System.entity.ServiceAppointment;
import com.example.Ev.System.entity.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InvoiceDataDto {
    private ServiceAppointment appointment;
    private List<ServiceType> services;
    private List<PartUsage> parts;
}
