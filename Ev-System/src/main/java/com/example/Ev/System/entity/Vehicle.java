package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

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

    // Mới từ DB
    @Column(name = "color", columnDefinition = "text")
    private String color;

    // Lưu ý: DB đang dùng tên cột 'licensen_plate' (có thể là typo). Map đúng tên cột hiện có trong DB.
    @Column(name = "license_plate", columnDefinition = "text")
    private String licensePlate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "deleted", nullable = false)
    @ColumnDefault("false")
    private boolean deleted = false;

    @OneToMany(mappedBy = "vehicle")
    private Set<ServiceAppointment> serviceAppointments = new LinkedHashSet<>();
}