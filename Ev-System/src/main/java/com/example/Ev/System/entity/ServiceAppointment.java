package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class ServiceAppointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_center_id", nullable = false)
    private ServiceCenter serviceCenter;

    @Column(name = "appointment_date", nullable = false)
    private Instant appointmentDate;

    @Nationalized
    @ColumnDefault("'pending'")
    @Column(name = "status", length = 20)
    private String status;

    @ColumnDefault("sysdatetime()")
    @Column(name = "created_at")
    private Instant createdAt;

    // 🔗 Link to AppointmentService join table
    @OneToMany(mappedBy = "appointment",
            cascade = CascadeType.ALL,   // <-- important
            orphanRemoval = true)
    private Set<AppointmentService> appointmentServices = new HashSet<>();

    public Set<AppointmentService> getAppointmentServices() {
        return appointmentServices;
    }

    public void setAppointmentServices(Set<AppointmentService> appointmentServices) {
        this.appointmentServices = appointmentServices;
    }


}
