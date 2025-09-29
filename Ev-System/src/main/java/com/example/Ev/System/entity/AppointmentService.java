package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Entity
public class AppointmentService {
    @EmbeddedId
    private AppointmentServiceId id;

    @MapsId("appointmentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private ServiceAppointment appointment;

    @MapsId("serviceTypeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    public AppointmentService() {
    }
}
