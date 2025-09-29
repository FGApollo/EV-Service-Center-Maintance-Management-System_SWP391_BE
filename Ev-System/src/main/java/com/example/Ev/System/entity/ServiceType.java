package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ServiceType")
public class ServiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_type_id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Nationalized
    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_est")
    private Integer durationEst;

    // 🔗 Link to AppointmentService join table
    @OneToMany(mappedBy = "serviceType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppointmentService> appointmentServices = new HashSet<>();
}
