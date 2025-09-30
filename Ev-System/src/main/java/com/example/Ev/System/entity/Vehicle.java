package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "vin", nullable = false, length = 50)
    private String vin;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "battery_capacity", precision = 10, scale = 2)
    private BigDecimal batteryCapacity;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "vehicle")
    private Set<ServiceAppointment> serviceAppointments = new LinkedHashSet<>();

}